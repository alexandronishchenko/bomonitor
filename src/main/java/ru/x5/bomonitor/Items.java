package ru.x5.bomonitor;

import java.sql.SQLException;

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
