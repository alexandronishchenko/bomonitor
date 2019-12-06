package ru.x5.bomonitor.Services.ZQL;

import ru.x5.bomonitor.JMXclient.JMXController;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.Services.nativ.bo.FullDiag;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.zabbixagentcli.ZabbixProxing;

import java.io.IOException;
import java.util.ArrayList;

public class Composer {
    private static Logger loger = bomonitor.getLogger();
    public String comand;
    ArrayList<String> directives = new ArrayList<>();
    Service job;

    public Composer(String comand) {
        this.comand = comand;
    }

    public String getResult() {
        this.directives = getServiceParams(this.comand);
        String serviceKind = this.directives.get(0);
        loger.insertRecord(this, "JOB will: " + directives.get(0) + ", kind: |" + serviceKind + "|", LogLevel.debug);
        switch (serviceKind) {
            case "jmx":
                if (!bomonitor.properties.getProperty("services").contains("jmxcli")) {
                    System.out.println("jmxcli service is disabled!");
                    loger.insertRecord(JMXController.class.getName(), "jmxcli service is disabled!", LogLevel.warn);
                    job = null;
                } else {
                    job = JMXController.getJMXCli();
                    loger.insertRecord(this, "JMX job was setted:" + directives.toString(), LogLevel.debug);
                }
                break;
            case "native":
                //get by request. To run log parsing service - enable at properties.
                job = new NativeService();
                loger.insertRecord(this, "Native job was setted:" + directives.toString(), LogLevel.debug);
                break;
            case "log":
                job = new LogService();
                loger.insertRecord(this, "Log job was setted:" + directives.toString(), LogLevel.debug);
                break;
            case "fulldiag":
                job = new FullDiag();
                loger.insertRecord(this, "Fulldiag job was setted:" + directives.toString(), LogLevel.debug);

                break;
            case "hw":
                job = new HardWareService();
                loger.insertRecord(this, "Hardware job was setted:" + directives.toString(), LogLevel.debug);

                break;
        }
        try {
            job.setDirectives(directives);
        } catch (NullPointerException e) {
            loger.insertRecord(this, "No job was setted:" + directives.toString(), LogLevel.warn);
        }
        String res = null;
        try {
            res = job.getMetric();
        } catch (NullPointerException e) {
            loger.insertRecord(this, "No integrated job was seted, try to resend to zabbix-agent:" + directives.toString(), LogLevel.info);
            try {
                if (!bomonitor.properties.getProperty("services").contains("zabbixagentcli")) {
                    loger.insertRecord(this, "Zabbix agent cli is disabled.", LogLevel.warn);
                    System.out.println("Zabbix agent cli is disabled.");
                    return null;
                }
                if (!directives.isEmpty()) {
                    ZabbixProxing zp = new ZabbixProxing();
                    String zabRes = zp.sendRequestToClient(comand);
                    System.out.println(zabRes);
                    res = zabRes;
                    loger.insertRecord(this, "Succed send. Result='" + res + "'", LogLevel.debug);
                }
            } catch (IOException g) {
                loger.insertRecord(this, "Zabbix to client resend failed.", LogLevel.error);
            }
        }
        return res;
    }

    ArrayList<String> getServiceParams(String s) {
        System.out.println(s);
        ArrayList<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                result.add(s.substring(0, i));
                s = s.substring(i + 1);
                i = 0;
            }
        }
        result.add(s);
        return result;
    }
}
