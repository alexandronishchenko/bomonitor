package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.database.DBConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.SQLqueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
@ServiceNative("Мониторинг БД")
public class DB extends ParrentNativeService {
    public DB() {
        this.name="db";
        this.value="";
    }

    //  @Override
    public String get(String directive) {
        String result=null;
        try {
        switch (directive){
            case "activerequests":
                    result=String.valueOf(getActiveRequests());
                break;
            case "frozentransaction":
                result=String.valueOf(getFrozenTransactions());
                break;
            case "long":
                result=String.valueOf(getLongOperations());
                break;
            case "counterrsap":
                result=String.valueOf(getCountErrSap());
                break;
            case "stractiverequests":
                 result=getStringActiveRequests();
                break;
            case "strfrozentransaction":
                result=getStringFrozenTransactions();
                break;
            case "erroridocs":
                result= getStringErrIdoc();
                break;

        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
        public String get(String directive,String subquery) {
        String res="";
        switch (directive){
            case "autovacuum":
                try {
                    res= String.valueOf(getAutoVacuum(subquery));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

        }
        return res;
    }
    @ZabbixRequest("native.db.activerequests")
    @Metric("Активные сессии в БД")
    public int getActiveRequests() throws SQLException {
        HashMap<String,String> map = DBConnection.executeSelect(SQLqueries.COUNT_ACTIVE_REQUESTS);
        return Integer.parseInt(map.get("count"));
    }
    @ZabbixRequest("native.db.stractiverequests")
    @StringMetric("Активные сессии в БД")
    public String getStringActiveRequests() throws SQLException {
        String result = DBConnection.getNote(SQLqueries.ACTIVE_REQUESTS).get("query");
        if(result.isEmpty() || result==null || result.equals("NULL") || result.equals("null")) return "";
        return result;
    }


    //not included at full diag, only numerics
    public int getAutoVacuum(String subquery) throws SQLException {
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date());
        HashMap<String,String> map;
        String query;
        if(subquery.equals("health")){
            query ="SELECT count(*) FROM pg_stat_user_tables where schemaname = 'gkretail' and autovacuum_count > 0 and cast(last_autovacuum as date)='"+date+"'";
        }else if(subquery.equals("avg")){
            query ="SELECT  sum(autovacuum_count) as \"count\" FROM pg_stat_user_tables where schemaname = 'gkretail'";
        }else if (subquery.contains("table:")){
            String[] arr=subquery.split(":");
            String table= arr[1];
            query ="SELECT  autovacuum_count as \"count\" FROM pg_stat_user_tables where schemaname = 'gkretail' and relname='"+table+"'";
        }else{
            return 0;
        }
        map=DBConnection.executeSelect(query);
        return Integer.parseInt(map.get("count"));

    }

    @ZabbixRequest("native.db.autovacuum.health")
    @Metric("Проходит ли автовакуум")
    public int getAutoVacuum() throws SQLException {
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date());
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_AUTOVACUUM,"count",new String[]{date}).get("count"));
    }
    @StringMetric("Проходит ли автовакуум")
    public String getStringAutoVacuum() throws SQLException {
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date());
        return DBConnection.executeSelect(SQLqueries.COUNT_AUTOVACUUM,"count",new String[]{date}).get("count");
    }

    @ZabbixRequest("native.db.frozentransaction")
    @Metric("Зависшие запросы количество")
    public int getFrozenTransactions() throws SQLException {
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_FROZEN_QUERIES).get("count"));
    }
    @ZabbixRequest("native.db.strfrozentransaction")
    @StringMetric("Зависшие запросы")
    public String getStringFrozenTransactions() throws SQLException {
        String result="";
        String met=DBConnection.getNote(SQLqueries.FROZEN_QUERIES).get("query");
        if(met!=null && !met.equals("NULL") && !met.equals("null")){
            result=met;
        }else{
            result="";
        }
        return result;
    }


    @Metric("Длинные операции в АПП к БД")
    public int getLongOperations(){
//TODO: grep for log???
        return 0;
    }


    @Metric("Ошибки сообщений SAP")
    public int getCountErrSap() throws SQLException {
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_SAP_ERRORS_TX).get("count"))+
                Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_SAP_ERRORS_RX).get("count"));
    }
    @ZabbixRequest("native.db.erroridocs")
    @StringMetric("Ошибки сообщений SAP")
    public String getStringErrIdoc() throws SQLException {
        String result="";
        String s1=DBConnection.getNote(SQLqueries.SAP_ERRORS_TX).get("msgtype");
        String s2=DBConnection.getNote(SQLqueries.SAP_ERRORS_RX).get("msgtype");
        if(s1==null || s1.equals("null")){result+= "";}else {
            result+=s1+";";
        }
        if(s2==null || s1.equals("null")){result+= "";}else {
            result+=s2+";";
        }
        if(result.isEmpty()) result="";
        return result;
    }
}
