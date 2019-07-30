package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.sql.SQLException;
@ServiceUnit
public class Stock implements Service {
    @Override
    public int get(String directive) {
        int res=0;
        try {
            res= getErrorFormat();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }

    public int getErrorFormat() throws SQLException {
        String s="select count(*) from GK_STOCK_LEDGER_ACCOUNT where cast(CURRENT_UNIT_COUNT as TEXT) like '%.____%'";
        return Integer.parseInt(DBConnection.executeSelect(s).get("count"));
    }
}
