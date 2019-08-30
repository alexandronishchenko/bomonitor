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
@ServiceUnit("Контроль цен")
public class Prices implements Service {
    @Override
    public String get(String directive) {
        String result="";
        try {
            if(directive.equals("errorchange")){
                    result= String.valueOf(getErrorChange());
            }else if(directive.equals("strerrorchange")) {
                result= getStringErrorChange();
            }
        }
         catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }


    @Metric("ошибки в изменении цен")
    public int getErrorChange() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        int subres1=0;
        try {
            subres1 = Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_PRICE_GK,"count",new String[]{date}).get("count")) +
                    Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_PRICE_CHANGE,"count",new String[]{date}).get("count"));
        }catch (NumberFormatException e){
            System.out.println("NULL returned at errors");
        }
        int subres2=0;
        try {
            subres2 = Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_UNPRINTED).get("count"));
        }catch (NumberFormatException e){
            System.out.println("NULL at 3-rd query PRICES.");
        }
        return subres1+subres2;
    }
    @StringMetric("ошибки в изменении цен")
    public String getStringErrorChange() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String result="";
        String s1 = DBConnection.executeSelect(SQLqueries.PRICE_GK_ERR,"item_id",new String[]{date}).get("item_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        String s2= DBConnection.executeSelect(SQLqueries.PRICE_CHANGE_ERR,"item_id",new String[]{date}).get("item_id");
        if(s2!=null&&!s2.equals("null")&&!s1.equals("NULL"))result+=s2;
        String s3 =DBConnection.getNote(SQLqueries.UNPRINTED_PRICES).get("item_id");
        if(s3!=null&&!s3.equals("null")&&!s1.equals("NULL"))result+=s3;
        return result;
    }
}
