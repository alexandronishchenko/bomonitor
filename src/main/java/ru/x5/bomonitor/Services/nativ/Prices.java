package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
@ServiceUnit
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
        int subres1=0;
        try {
            subres1 = Integer.parseInt(DBConnection.executeSelect(query1).get("count")) + Integer.parseInt(DBConnection.executeSelect(query2).get("count"));
        }catch (NumberFormatException e){
            System.out.println("NULL returned at errors");
        }
        int subres2=0;
        String query3="select count (*) item_id from gk_price_change_control where status in ('NEW', 'PRINTED') and price_type_code in ('00', '01') group by item_id having count(*)>1";
        try {
            subres2 = Integer.parseInt(DBConnection.executeSelect(query3).get("count"));
        }catch (NumberFormatException e){
            System.out.println("NULL at 3-rd query PRICES.");
        }
        //int fullres=subres1+subres2;
        return subres1+subres2;
    }
}
