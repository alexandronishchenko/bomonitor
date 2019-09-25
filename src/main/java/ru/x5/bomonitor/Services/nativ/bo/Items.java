package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.database.DBConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.SQLqueries;

import java.sql.SQLException;
@ServiceNative("Товары")
public class Items implements ServiceNativeInterface {

    @Override
    public String get(String directive) {
        String result="";
        try {
            if (directive.equals("getdiff")) {
                result=String.valueOf(getDiff());
            } else if (directive.equals("strgetdiff")) {
                result=regetStringDiff();
            }
        }catch (SQLException e){
            System.out.println("SQL exception");
        }
        return result;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }
@Metric("разница в БД")
    public int getDiff() throws SQLException{
        String query = "select count(ID_ITM) from as_itm t left join XRG_ITEM k on t.id_itm = k.item_id where k.item_id is null";
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_ITEMS_DIFF).get("count"));
    }
    @StringMetric("разница в БД")
    public String regetStringDiff() throws SQLException{
        String result="";
        String s1 =DBConnection.getNote(SQLqueries.ITEMS_DIFF).get("ID_ITM");
        if(s1==null || s1.equals("NULL") || s1.equals("null"))return "";
        return result;
    }
}
