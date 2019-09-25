package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.database.DBConnection;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.SQLqueries;

import java.sql.SQLException;
@ServiceNative("Диспетчер заданий")
public class Taskmanager implements ServiceNativeInterface {
    @Override
    public String get(String directive) {
        String res="";
        try {
            if (directive.equals("tasknottop")){
                res=String.valueOf(getTaskNotTop());
            }else if(directive.equals("strtasknottop")){
                res=getStringTaskNotTop();
            }else{
                res="";
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


    @Metric("задания не из ТОП")
    public int getTaskNotTop() throws SQLException {

        return Integer.parseInt(DBConnection.executeSelect(SQLqueries.COUNT_TASKS_NOT_TOP).get("count"));
    }
    @StringMetric("задания не из ТОП")
    public String getStringTaskNotTop() throws SQLException {
        String result="";
        String s1 = DBConnection.executeSelect(SQLqueries.TASKS_NOT_TOP).get("task_mgmt_ga_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        return result;
    }


}
