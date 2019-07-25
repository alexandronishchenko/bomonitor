package ru.x5.bomonitor;

import java.sql.SQLException;
import java.util.HashMap;

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

    public int getTmpTables() throws SQLException {
        String query = "select count(CREATION_TIMESTAMP) from XRG_EGAIS_EXCISE_STAMPS_TMP where CREATION_TIMESTAMP < now()";
        HashMap<String,String> map = DBConnection.executeSelect(query);
        return Integer.parseInt(map.get("count"));
    }
}
