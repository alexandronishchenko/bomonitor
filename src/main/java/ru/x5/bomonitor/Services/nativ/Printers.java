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
@ServiceUnit("Принтеры")
public class Printers implements Service {
    @Override
    public String get(String directive) {
        String result="";
        try {
            if(directive.equals("queue")){
                result=String.valueOf(getQueue());
            }else if(directive.equals("strqueue")){
                result=String.valueOf(getStringQueue());
            }else{
                result="";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }


@Metric("очередь")
    public int getQueue() throws SQLException {
        long dt = new Date().getTime()-(2*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = smp.format(new Date(dt));
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_QUEUE_PRINTER,"count",new String[]{date}).get("count"));
    }
    @StringMetric("очередь")
    public String getStringQueue() throws SQLException {
        String result="";
        long dt = new Date().getTime()-(2*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = smp.format(new Date(dt));
        String s1=DBConnection.executeSelect(SQLqueries.QUEUE_PRINTER,"task_id",new String[]{date}).get("task_id");
        if(s1!=null)result+=s1;
        return result;
    }
}
