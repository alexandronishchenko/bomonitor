package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.PostgresSQLqueries;

import java.sql.SQLException;
@ServiceNative("Товары")
public class Items extends ParrentNativeService {
    public Items() {
        this.name="items";
    }

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
@Metric(value = "Количество отличий в БД xrg - gk",directive = "native.items.getdiff")
    public int getDiff() throws SQLException{
        String query = "select count(ID_ITM) from as_itm t left join XRG_ITEM k on t.id_itm = k.item_id where k.item_id is null";
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_ITEMS_DIFF).get("count"));
    }
    @StringMetric(value = "разница в БД",directive = "native.items.strgetdiff")
    public String regetStringDiff() throws SQLException{
        String result="";
        String s1 = PostgresConnection.getNote(PostgresSQLqueries.ITEMS_DIFF).get("ID_ITM");
        if(s1==null || s1.equals("NULL") || s1.equals("null"))return "";
        return result;
    }
}
