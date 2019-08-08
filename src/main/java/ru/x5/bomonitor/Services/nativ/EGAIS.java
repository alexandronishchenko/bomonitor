package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;
import ru.x5.bomonitor.StringMetric;
import ru.x5.bomonitor.ZQL.SQLqueries;

import java.sql.SQLException;
import java.util.HashMap;
@ServiceUnit("ЕГАИС")
public class EGAIS implements Service {
    @Override
    public String get(String directive) {
        try {
        if(directive.equals("tmptables")){

                return String.valueOf(getTmpTables());

        }else if(directive.equals("strtmptables")){
            return getStringTmpTables();
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }
@Metric("временные таблицы не очищаются")
    public int getTmpTables() throws SQLException {
        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.EGAIS_COUNT_TMP).get("count"));
    }
    @StringMetric("временные таблицы не очищаются")
    public String getStringTmpTables() throws SQLException {
        String result=DBConnection.getNote(SQLqueries.EGAIS_CREATION_TSTP).get("CREATION_TIMESTAMP");
        if(result==null)return "";
        if(result.isEmpty() ) return "";
        if(result.equals("NULL"))return "";
        return result;
    }
}
