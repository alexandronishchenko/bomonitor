package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;
import ru.x5.bomonitor.StringMetric;
import ru.x5.bomonitor.ZQL.SQLqueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
@ServiceUnit("Мониторинг БД")
public class DBMonitoring implements Service {

  //  @Override
    public String get(String directive) {
        String result="0";
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
        String res="0";
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


@Metric("Активные сессии в БД")
    public int getActiveRequests() throws SQLException {
        HashMap<String,String> map = DBConnection.executeSelect(SQLqueries.COUNT_ACTIVE_REQUESTS);
        return Integer.parseInt(map.get("count"));
    }
    @StringMetric("Активные сессии в БД")
    public String getStringActiveRequests() throws SQLException {
        String result = DBConnection.getNote(SQLqueries.ACTIVE_REQUESTS).get("query");
        if(result.isEmpty() || result==null || result.equals("NULL")) return " ";
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


    @Metric("Зависшие запросы")
    public int getFrozenTransactions() throws SQLException {
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_FROZEN_QUERIES).get("count"));
    }
    @StringMetric("Зависшие запросы")
    public String getStringFrozenTransactions() throws SQLException {
        String result="";
        String met=DBConnection.getNote(SQLqueries.FROZEN_QUERIES).get("query");
        if(!met.isEmpty() || met!=null || met!=""){
            result=met;
        }else{
            result=" ";
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
        if(result.isEmpty()) result=" ";
        return result;
    }
}
