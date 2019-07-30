package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
@ServiceUnit
public class Printers implements Service {
    @Override
    public int get(String directive) {
        try {
            return getQueue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }

    public int getQueue() throws SQLException {
        long dt = new Date().getTime()-(2*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = smp.format(new Date(dt));
        String query = "select count(*) from gk_raw_change_event WHERE STATUS!='PROCESSED' and CREATED_TIMESTAMP < '"+date+"' and type_code in ('CHANGE_EVENT', 'PARAM.ITEM')";
        return Integer.parseInt(DBConnection.executeSelect(query).get("count"));
    }
}
