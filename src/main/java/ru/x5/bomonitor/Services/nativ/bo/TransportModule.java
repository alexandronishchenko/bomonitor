package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.database.DBConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.database.SQLqueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
@ServiceNative("Транспортный модуль")
public class TransportModule implements ServiceNativeInterface {
    @Override
    public String get(String directive) {
        String res="";

        try {
            if (directive.equals("errors")){
                res=String.valueOf(getErrors());
            }else if(directive.equals("strerrors")){
                res=getStringErrors();
            }else{
                res="";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }


    @Metric("ошибки")
    public int getErrors() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));

        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_TRANSPORT_ERRORS,"count",new String[]{date}).get("count"));
    }
    @Metric("ошибки")
    public String getStringErrors() throws SQLException {
        String result="";
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String s1 = DBConnection.executeSelect(SQLqueries.TRANSPORT_ERRORS,"bon_seq_id",new String[]{date}).get("bon_seq_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;
    }
}
