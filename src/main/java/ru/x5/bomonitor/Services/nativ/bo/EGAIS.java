package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.database.DBConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.SQLqueries;

import java.sql.SQLException;

@ServiceNative("ЕГАИС")
public class EGAIS extends ParrentNativeService {
    public EGAIS() {
        this.name="egais";
        this.value="";
    }

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
