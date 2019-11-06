package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.PostgresSQLqueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
@ServiceNative("Чеки")
public class Reciepts extends ParrentNativeService {
    public Reciepts() {
        this.name="reciepts";
    }

    @Override
    public String get(String directive) {
        String res=null;
        try{
            switch (directive){
                case "balancediff":
                    res= String.valueOf(getBalanceDiff());
                    break;
                case "duplicatebon":
                    res= String.valueOf(getDuplicatesBon());
                    break;
                case "incorrectbon":
                    res= String.valueOf(getIncorrectBonnr());
                    break;
                case "queue":
                    res= String.valueOf(getQueue());
                    break;
                case "stockandreciept":
                    res= String.valueOf(getStockAndReciept());
                    break;
                case "strbalancediff":
                    res= getStringBalanceDiff();
                    break;
                case "strduplicatebon":
                    res= getStringDuplicatesBon();
                    break;
                case "strincorrectbon":
                    res= getStringIncorrectBonnr();
                    break;
                case "strqueue":
                    res= getStringQueue();
                    break;
                case "strstockandreciept":
                    res= getStringStockAndReciept();
                    break;
            }
        }catch (SQLException e){
            e.printStackTrace();

        }
        return res;
    }

    @Override
    public String get(String directive, String subquery) {
        return "0";
    }

    @Metric(value = "расхождение баланса число",directive = "native.reciepts.balancediff")
    public int getBalanceDiff() throws SQLException {
               //Integer.parseInt()
        Double d = 0.0;
        try {
             d = Double.parseDouble(PostgresConnection.executeSelect(PostgresSQLqueries.BALANCE_DIFF).get("count"));
        }catch (NullPointerException e){
            System.out.println("NULL was returned.");
        }
        return d.intValue();
    }
    @StringMetric(value = "расхождение баланса",directive = "native.reciepts.strbalancediff")
    public String getStringBalanceDiff() throws SQLException {
        String result=null;
        result= PostgresConnection.executeSelect(PostgresSQLqueries.BALANCE_DIFF).get("count");
        if(result==null||result.equals("null")||result.equals("NULL"))return "";
        return result;
    }

    @Metric(value = "задвоенный номер количество",directive = "native.reciepts.duplicatebon")
    public int getDuplicatesBon() throws SQLException {
        long dt = new Date().getTime()-(3*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.DUPLICATE_BONNR_COUNT,"bonnr",new String[]{date}).get("count"));
    }
    @StringMetric(value = "задвоенный номер",directive = "native.reciepts.ыекduplicatebon")
    public String getStringDuplicatesBon() throws SQLException {
        String result=null;
        long dt = new Date().getTime()-(3*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        result= PostgresConnection.executeSelect(PostgresSQLqueries.DUPLICATE_BONNR,"bonnr",new String[]{date}).get("bonnr");
        //System.out.println(result);
        if(result==null||result.equals("null")||result.equals("null"))return "";
        return result;
    }

    @Metric(value = "Некоректный номер чека",directive = "native.reciepts.incorrectbon")
    public int getIncorrectBonnr() throws SQLException {
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.INCORRECT_BONNR).get("count"));
    }
    @StringMetric(value = "Номер некорректного чека",directive = "native.reciepts.strincorrectbon")
    public String getStringIncorrectBonnr() throws SQLException{
        String result = PostgresConnection.executeSelect(PostgresSQLqueries.INCORRECT_BONNR_STR).get("bonnr");
        if(result.isEmpty() || result==null || result.equals("NULL")|| result.equals("null")) return "";
        return result;
    }


    @Metric(value = "очередь чеков количественно",directive = "native.reciepts.queue")
    int getQueue() throws SQLException {
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.QUEUE_RECIEPTS).get("count"));
    }
    @StringMetric(value = "очередь чеков",directive = "native.reciepts.strqueue")
    String getStringQueue() throws SQLException {
        String result= PostgresConnection.getNote(PostgresSQLqueries.QUEUE_RECIEPTS_STR).get("TRANSACTION_SEQ_ID");
        if(result==null)return "";
        if(result.isEmpty() ) return "";
        if(result.equals("NULL"))return "";
        if(result.equals("null"))return "";
        return result;
    }

    @Metric(value = "сверка чек - остаток число",directive = "native.reciepts.stockandreciept")
    int getStockAndReciept() throws SQLException {
        long dt = new Date().getTime()-(10*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.STOCK_RECIEPT1,"count",new String[]{date,date}).get("count"))+
                Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.STOCK_RECIEPT2,"count",new String[]{date,date}).get("count"));

    }
    @StringMetric(value = "сверка чек - остаток",directive = "native.reciepts.stockandreciept")
    String getStringStockAndReciept() throws SQLException {
        String result="";
        long dt = new Date().getTime()-(10*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        result= String.valueOf(Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.STOCK_RECIEPT1,"count",new String[]{date,date}).get("count"))+
                Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.STOCK_RECIEPT2,"count",new String[]{date,date}).get("count")));
        if(result==null)return "";
        if(result.isEmpty() ) return "";
        if(result.equals("NULL"))return "";
        if(result.equals("null"))return "";
        return result;
    }
}
