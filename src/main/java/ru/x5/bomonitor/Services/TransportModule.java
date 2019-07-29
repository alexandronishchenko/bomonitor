package ru.x5.bomonitor.Services;

import ru.x5.bomonitor.DBConnection;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
@ServiceUnit
public class TransportModule implements Service {
    @Override
    public int get(String directive) {
        int res=0;

        try {
            res=getErrors();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }
    public int getErrors() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String s="select count(*) from (select bon_seq_id from XRG_TRANSPORT_MODULE group by bon_seq_id having count(bon_seq_id)=1) r,\n" +
                " xrg_transport_module d where d.ERROR_DESCRIPTION is not null and d.TIMESTAMP > '"+date+"' and r.bon_seq_id = d.bon_seq_id";
        return Integer.parseInt(DBConnection.executeSelect(s).get("count"));
    }
}
