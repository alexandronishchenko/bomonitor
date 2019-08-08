package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;
import ru.x5.bomonitor.StringMetric;
import ru.x5.bomonitor.ZQL.SQLqueries;

import java.sql.SQLException;
@ServiceUnit("Товары")
public class Items implements Service {

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
        if(s1==null)return "";
        return result;
    }
}
