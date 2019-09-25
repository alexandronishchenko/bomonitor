package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.database.DBConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.SQLqueries;

import java.sql.SQLException;
import java.util.HashMap;
@ServiceNative("Лояльность")
public class Loyalty extends  ParrentNativeService{
    public Loyalty() {
        this.name="loyalty";
    }

    public String get(String directive){
        String result=null;
        try {
            if (directive.equals("counter")) {
                return String.valueOf(getStoppedCounters());
            } else if (directive.equals("long")) {
                return String.valueOf(getLongUnsetTransactions());
            } else if (directive.equals("unsent")) {
                return String.valueOf(getUnsentTransactions());
            } else if (directive.equals("strcounter")) {
                return getStringStoppedCounters();
            } else if (directive.equals("strlong")) {
                return getStringLongUnsetTransactions();
            } else if (directive.equals("strunsent")) {
                return getStringUnsentTransactions();
            } else {
                return String.valueOf(getStoppedCounters() + getUnsentTransactions() + getLongUnsetTransactions());
            }
        }catch (SQLException e) {
            result=null;
        }
        return result;

    }
    public  String get(String directive,String subquery){
        return "";
    }

    @Metric("Остановленные счетчики")
    public int getStoppedCounters()throws SQLException{
            return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_STOPPED_COUNTERS).get("count"));
    }
    @StringMetric("Остановленные счетчики")
    public String getStringStoppedCounters()throws SQLException{
        String result="";
        String s1=DBConnection.getNote(SQLqueries.STOPPED_COUNTERS).get("upload_type_code");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;

    }


@Metric("Давно зависшие транзакции")
    public int getLongUnsetTransactions()throws SQLException{
           return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_OLD_UNSENT_TRANSACTIONS).get("count"));
    }
    @StringMetric("Давно зависшие транзакции")
    public String getStringLongUnsetTransactions()throws SQLException{
        String result="";
        String s1 = DBConnection.getNote(SQLqueries.OLD_UNSENT_TRANSACTIONS).get("bon_seq_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
            return result;


    }



@Metric("Неотправленные транзакции")
    public int getUnsentTransactions()throws SQLException{
            HashMap<String,String> map = DBConnection.executeSelect(SQLqueries.COUNT_UNSENT1);
            HashMap<String,String> map2 = DBConnection.executeSelect(SQLqueries.COUNT_UNSENT2);
            return Integer.parseInt(map.get("count"))+ Integer.parseInt(map2.get("count"));
    }
    @StringMetric("Неотправленные транзакции")
    public String getStringUnsentTransactions()throws SQLException{
        String result="";
        String s1 = DBConnection.getNote(SQLqueries.UNSENT1).get("bon_seq_id");
        String s2 = DBConnection.getNote(SQLqueries.UNSENT2).get("bon_seq_id");
        if(!s1.equals("null")&&s1!=null)result+=s1;
        if(s2.equals("null")&&s2!=null)result+=s2;
        return result;

    }

}
