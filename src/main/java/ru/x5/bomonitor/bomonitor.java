package ru.x5.bomonitor;

import ru.x5.bomonitor.ru.x5.bomonitor.threading.ZabbixImitation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;

public class bomonitor {
//    static Properties properties = new Properties();
    static HashMap<String,Service> mapping = new HashMap<>();
    static Properties properties;
    static {
        //"/etc/zabbix/agentjar.properties"
        properties=new Properties();
        try {
            properties.load(new FileInputStream("/etc/zabbix/agentjar.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapping.put("loyalty", new Loyalty());
        mapping.put("db", new DBMonitoring());
        mapping.put("egais", new EGAIS());
        mapping.put("prices", new Prices());
        mapping.put("items", new Items());
        mapping.put("printers", new Printers());
        mapping.put("reciepts", new Reciepts());
        mapping.put("stock", new Stock());
        mapping.put("taskmanager", new Taskmanager());
        mapping.put("transportmodule", new TransportModule());
        mapping.put("action", new Action());
    }

    public static void main(String[] args) {
        String subquery=null;
        String service=null;
        String param=null;
        if(args.length==1){
            System.out.println("testing zabbix");
            ZabbixImitation zi = new ZabbixImitation(Integer.parseInt(properties.getProperty("port")));
            new Thread(zi).start();
        }else if(args.length==3){
            service=args[0];
            param=args[1];
            subquery=args[2];
            System.out.println(String.valueOf(mapping.get(service).get(param,subquery)));
        }else if(args.length==2){
            service=args[0];
            param=args[1];
            System.out.println(String.valueOf(mapping.get(service).get(param)));
        }else {
            System.out.println("incorrect param");
        }



    }

}
