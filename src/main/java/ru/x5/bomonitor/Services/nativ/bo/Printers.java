package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.PostgresSQLqueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
@ServiceNative("Принтеры")
public class Printers extends ParrentNativeService {
    public Printers() {
        this.name="printers";
    }

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


@Metric(value = "очередь",directive = "native.printers.queue")
    public int getQueue() throws SQLException {
        long dt = new Date().getTime()-(2*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = smp.format(new Date(dt));
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_QUEUE_PRINTER,"count",new String[]{date}).get("count"));
    }
    @StringMetric(value = "очередь",directive = "native.printers.strqueue")
    public String getStringQueue() throws SQLException {
        String result="";
        long dt = new Date().getTime()-(2*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = smp.format(new Date(dt));
        String s1= PostgresConnection.executeSelect(PostgresSQLqueries.QUEUE_PRINTER,"task_id",new String[]{date}).get("task_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;
    }
}
