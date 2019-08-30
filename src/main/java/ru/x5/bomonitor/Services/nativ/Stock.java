package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;
import ru.x5.bomonitor.StringMetric;
import ru.x5.bomonitor.ZQL.SQLqueries;

import java.sql.SQLException;
@ServiceUnit("Остатки")
public class Stock implements Service {
    @Override
    public String get(String directive) {
        String result="";
        try {
            if(directive.equals("errorformat")){
                result= String.valueOf(getErrorFormat());
            }else if(directive.equals("strerrorformat")){
                result= getStringErrorFormat();
            }else{
                result= "";
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


@Metric("ошибки формата записи")
    public int getErrorFormat() throws SQLException {
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_STOCK_ERR).get("count"));
    }
    @StringMetric("ошибки формата записи")
    public String getStringErrorFormat() throws SQLException {
        String result="";
        String s1 = DBConnection.executeSelect(SQLqueries.STOCK_ERR).get("item_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;
    }
}
