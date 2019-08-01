package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.sql.SQLException;
import java.util.HashMap;
@ServiceUnit("ЕГАИС")
public class EGAIS implements Service {
    @Override
    public int get(String directive) {
        if(directive.equals("tmptables")){
            try {
                return getTmpTables();
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
@Metric("временные таблицы не очищаются")
    public int getTmpTables() throws SQLException {
        String query = "select count(CREATION_TIMESTAMP) from XRG_EGAIS_EXCISE_STAMPS_TMP where CREATION_TIMESTAMP < now()";
        HashMap<String,String> map = DBConnection.executeSelect(query);
        return Integer.parseInt(map.get("count"));
    }
}
