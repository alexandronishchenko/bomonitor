package ru.x5.bomonitor.ZQL;


import ru.x5.bomonitor.Action;
import ru.x5.bomonitor.DBConnection;
import ru.x5.bomonitor.Services.*;
import ru.x5.bomonitor.queries.SQLnativeQueries;
import ru.x5.bomonitor.ru.x5.bomonitor.threading.ZabbixImitation;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class NativeService extends Service {

    static HashMap<String, ru.x5.bomonitor.Services.Service> mapping = new HashMap<>();
    static {
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

    @Override
    //native|duplicatebon|count|sum|72
    //native|stockandrec1&stockandrec2|count|sum
    public String getMetric(){
        String result ="";
//        String[] queries = getQueries();
//        String[] results=new String[queries.length];
//        String date="";
//        if(directives.get(4)!=null){
//            date=getDate(Integer.parseInt(directives.get(4)));
//        }
//        for (int i = 0; i < queries.length; i++) {
//            try {
//                results[i]= String.valueOf(DBConnection.executeSelect(SQLnativeQueries.getQuery(queries[i],date)).get(directives.get(3)));
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        if(directives.get(2).equals("sum")){
//            int meta=0;
//            for (int i = 0; i < results.length; i++) {
//                try {
//                    meta += Integer.parseInt(results[i]);
//                }catch (NumberFormatException e){
//                    System.out.println("Cannot parse "+results[i]+" to integer");
//                    e.printStackTrace();
//                }
//            }
//            result=String.valueOf(meta);
//        }
//
//        if(directives.get(2).equals("str")){
//            for (int i = 0; i < results.length; i++) {
//                result+=results[i];
//            }
//        }
        String subquery=null;
        String service=null;
        String param=null;
      try{
          if(directives.size()==4){//3 param
            service=directives.get(1);
            param=directives.get(2);
            subquery=directives.get(3);
            result = String.valueOf(mapping.get(service).get(param, subquery));
        }else if(directives.size()==3){//2 param
            service=directives.get(1);
            param=directives.get(2);
            result= String.valueOf(mapping.get(service).get(param));
        }else {//else count of param
            System.out.println("incorrect param");
        }
    }catch (NullPointerException e){
        //e.printStackTrace();
        System.out.println("No such service: "+ service+" -> "+param);
    }
        return result;

    }
    private String getDate(int hours){
        long dt = new Date().getTime()-(hours*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        return smp.format(new Date(dt));
    }
    private String[] getQueries(){
        String queries=this.directives.get(1);
        return queries.split("&");
    }

}
