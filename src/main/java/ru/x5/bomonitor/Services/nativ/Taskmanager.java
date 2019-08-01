package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.sql.SQLException;
@ServiceUnit("Диспетчер заданий")
public class Taskmanager implements Service {
    @Override
    public int get(String directive) {
        int res=0;
        try {
            res=getTaskNotTop();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }
    @Metric("задания не из ТОП")
    public int getTaskNotTop() throws SQLException {
        String s="SELECT count (*) FROM XRG_TASK_MGMT_GA_DETAILS \n" +
                "  WHERE PLU in (SELECT PLU FROM XRG_SALES_PROFILE WHERE PLU in \n" +
                "  (SELECT ITEM_ID FROM XRG_ITEM WHERE TOP_TYPECODE is null)\n" +
                "  and ACTIVE ='J')";
        return Integer.parseInt(DBConnection.executeSelect(s).get("count"));
    }


}
