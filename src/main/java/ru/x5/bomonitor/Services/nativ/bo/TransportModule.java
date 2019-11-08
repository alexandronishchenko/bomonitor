package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.database.PostgresConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.database.PostgresSQLqueries;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ServiceNative("Транспортный модуль")
public class TransportModule extends ParrentNativeService {
    public TransportModule() {
        this.name = "transportmodule";
    }

    @Override
    public String get(String directive) {
        String res = "";

        try {
            if (directive.equals("errors")) {
                res = String.valueOf(getErrors());
            } else if (directive.equals("strerrors")) {
                res = getStringErrors();
            } else {
                res = "";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }


    @Metric(value = "ошибки транспортного модуля количество", directive = "native.transportmodule.errors")
    public int getErrors() throws SQLException {
        long dt = new Date().getTime() - (10 * 24 * 60 * 60 * 1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));

        return Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_TRANSPORT_ERRORS, "count", new String[]{date}).get("count"));
    }

    @Metric(value = "ошибки транспортного модуля", directive = "native.transportmodule.strerrors")
    public String getStringErrors() throws SQLException {
        String result = "";
        long dt = new Date().getTime() - (10 * 24 * 60 * 60 * 1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String s1 = PostgresConnection.executeSelect(PostgresSQLqueries.TRANSPORT_ERRORS, "bon_seq_id", new String[]{date}).get("bon_seq_id");
        if (s1 != null && !s1.equals("null") && !s1.equals("NULL")) result += s1;
        return result;
    }
}
