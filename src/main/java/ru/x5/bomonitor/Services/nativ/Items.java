package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.sql.SQLException;
@ServiceUnit("Товары")
public class Items implements Service {

    @Override
    public int get(String directive) {
        return getDiff();
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }
@Metric("разница в БД")
    public int getDiff(){
        String query = "select count(CREATION_TIMESTAMP) from XRG_EGAIS_EXCISE_STAMPS_TMP where CREATION_TIMESTAMP < now()";
        try {
            return Integer.parseInt(DBConnection.executeSelect(query).get("count"));
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
