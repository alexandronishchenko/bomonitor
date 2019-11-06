package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.PostgresSQLqueries;

import java.sql.SQLException;
@ServiceNative("Остатки")
public class Stock extends ParrentNativeService {
    public Stock() {
        this.name="stock";
    }

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


@Metric(value = "ошибки формата записи",directive = "native.stock.errorformat")
    public int getErrorFormat() throws SQLException {
        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_STOCK_ERR).get("count"));
    }
    @StringMetric(value = "ошибки формата записи",directive = "native.stock.strerrorformat")
    public String getStringErrorFormat() throws SQLException {
        String result="";
        String s1 = PostgresConnection.executeSelect(PostgresSQLqueries.STOCK_ERR).get("item_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;
    }
}
