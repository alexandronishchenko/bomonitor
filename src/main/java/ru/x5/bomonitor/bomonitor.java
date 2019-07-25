package ru.x5.bomonitor;

import java.util.HashMap;

public class bomonitor {
//    static Properties properties = new Properties();
    static HashMap<String,Service> mapping = new HashMap<>();
    static {
        mapping.put("loyalty", new Loyalty());
        mapping.put("db", new DBMonitoring());
        mapping.put("egais", new EGAIS());
        mapping.put("prices", new Prices());
        mapping.put("items", new Items());
        mapping.put("printers", new Printers());
        mapping.put("reciepts", new Reciepts());
        mapping.put("action", new Action());
    }

    public static void main(String[] args) {
        String service=args[0];
        String param=args[1];
        String subquery=null;
        if(args.length==3){
            subquery=args[2];
            System.out.println(String.valueOf(mapping.get(service).get(param,subquery)));
        }else {
            System.out.println(String.valueOf(mapping.get(service).get(param)));
        }
    }

}
