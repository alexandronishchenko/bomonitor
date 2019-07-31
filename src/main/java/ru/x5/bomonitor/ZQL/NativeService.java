package ru.x5.bomonitor.ZQL;


import ru.x5.bomonitor.Action;
import ru.x5.bomonitor.Services.nativ.*;

import java.text.SimpleDateFormat;
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
        mapping.put("firebird", new Firebird());

        mapping.put("action", new Action());
    }

    @Override
    //native|duplicatebon|count|sum|72
    //native|stockandrec1&stockandrec2|count|sum
    public String getMetric(){
        String result ="";
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
