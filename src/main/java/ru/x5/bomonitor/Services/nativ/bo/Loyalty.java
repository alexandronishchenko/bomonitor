package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.PostgresSQLqueries;

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

    @Metric(value = "Остановленные счетчики",directive = "native.loyalty.counter")
    public int getStoppedCounters()throws SQLException{
            return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_STOPPED_COUNTERS).get("count"));
    }
    @StringMetric(value = "Остановленные счетчики",directive = "native.loyalty.strcounter")
    public String getStringStoppedCounters()throws SQLException{
        String result="";
        String s1= PostgresConnection.getNote(PostgresSQLqueries.STOPPED_COUNTERS).get("upload_type_code");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;

    }


@Metric(value = "Давно зависшие транзакции",directive = "native.loyalty.long")
    public int getLongUnsetTransactions()throws SQLException{
           return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_OLD_UNSENT_TRANSACTIONS).get("count"));
    }
    @StringMetric(value = "Давно зависшие транзакции",directive = "native.loyalty.strlong")
    public String getStringLongUnsetTransactions()throws SQLException{
        String result="";
        String s1 = PostgresConnection.getNote(PostgresSQLqueries.OLD_UNSENT_TRANSACTIONS).get("bon_seq_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
            return result;


    }



@Metric(value = "Неотправленные транзакции",directive = "native.loyalty.unsent")
    public int getUnsentTransactions()throws SQLException{
            HashMap<String,String> map = PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_UNSENT1);
            HashMap<String,String> map2 = PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_UNSENT2);
            return Integer.parseInt(map.get("count"))+ Integer.parseInt(map2.get("count"));
    }
    @StringMetric(value = "Неотправленные транзакции",directive = "native.loyalty.strunsent")
    public String getStringUnsentTransactions()throws SQLException{
        String result="";
        String s1 = PostgresConnection.getNote(PostgresSQLqueries.UNSENT1).get("bon_seq_id");
        String s2 = PostgresConnection.getNote(PostgresSQLqueries.UNSENT2).get("bon_seq_id");
        if(!s1.equals("null")&&s1!=null)result+=s1;
        if(s2.equals("null")&&s2!=null)result+=s2;
        return result;

    }

}
