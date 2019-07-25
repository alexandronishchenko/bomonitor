package ru.x5.bomonitor;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Prices implements Service {
    @Override
    public int get(String directive) {
        if(directive.equals("errorchange")){
            try {
                return getErrorChange();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }
    public int getErrorChange() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String query1="select count (*) from GK_PRICE_CHANGE_IMPORT where status = 'ERROR' and CREATION_TIMESTAMP > '"+date+"'";
        String query2="select count (*) from GK_PRICE_CHANGE_CONTROL where status = 'ERROR' and CREATION_TIMESTAMP > '"+date+"'";
        int subres1=Integer.parseInt(DBConnection.executeSelect(query1).get("count"))+Integer.parseInt(DBConnection.executeSelect(query2).get("count"));

        String query3="select count (*) item_id from gk_price_change_control where status in ('NEW', 'PRINTED') and price_type_code in ('00', '01') group by item_id having count(*)>1";
        int subres2=Integer.parseInt(DBConnection.executeSelect(query3).get("count"));

        return subres1+subres2;
    }
}
