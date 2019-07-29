package ru.x5.bomonitor.Services;

import ru.x5.bomonitor.DBConnection;

import java.sql.SQLException;
@ServiceUnit
public class Items implements Service {

    @Override
    public int get(String directive) {
        return getDiff();
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }

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
