package ru.x5.bomonitor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DBMonitoring implements Service{

    @Override
    public int get(String directive) {
        int result=0;
        try {
        switch (directive){
            case "activerequests":
                    result=getActiveRequests();
                break;
            case "frozentransaction":
                result=getFrozenTransactions();
                break;
            case "long":
                result=getLongOperations();
                break;
            case "tmptables":
                result=getTmpTables();
                break;
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public int get(String directive,String subquery) {
        int res=0;
        switch (directive){
            case "autovacuum":
                try {
                    res= getAutoVacuum(subquery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

        }
        return res;
    }



    public int getActiveRequests() throws SQLException {
        String query="select count(*) from pg_stat_activity";
        HashMap<String,String> map = DBConnection.executeSelect(query);
        return Integer.parseInt(map.get("count"));
    }
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

    public int getFrozenTransactions() throws SQLException {
        String query = "SELECT count(*) FROM pg_stat_activity WHERE xact_start < (CURRENT_TIMESTAMP - INTERVAL '1 hour')";
        HashMap<String,String> map=DBConnection.executeSelect(query);
        return Integer.parseInt(map.get("count"));
    }

    public int getLongOperations(){
//TODO: grep for log???
        return 0;
    }
    public int getTmpTables() throws SQLException {
        String query1="select count(errorcode) from XRG_SAP_PI_TX where status != 'OK'";
        String query2="select count(errorcode) from XRG_SAP_PI_RX where status != 'OK'";
        HashMap<String,String> m1 = DBConnection.executeSelect(query1);
        HashMap<String,String> m2 = DBConnection.executeSelect(query2);
        return Integer.parseInt(m1.get("count"))+Integer.parseInt(m2.get("count"));
    }
}
