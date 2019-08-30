package ru.x5.bomonitor;


import ru.x5.bomonitor.Services.*;
import ru.x5.bomonitor.ru.x5.bomonitor.zabbix.ZabbixImitation;


import java.util.ArrayList;
import java.util.HashMap;

public class SyncJob {

    static HashMap<String, Service> mapping = new HashMap<>();
    static {
//        mapping.put("loyalty", new Loyalty());
//        mapping.put("db", new DBMonitoring());
//        mapping.put("egais", new EGAIS());
//        mapping.put("prices", new Prices());
//        mapping.put("items", new Items());
//        mapping.put("printers", new Printers());
//        mapping.put("reciepts", new Reciepts());
//        mapping.put("stock", new Stock());
//        mapping.put("taskmanager", new Taskmanager());
//        mapping.put("transportmodule", new TransportModule());
//        mapping.put("heap", new Heap());
//        mapping.put("garbagecollector", new GarbageCollector1());
//        mapping.put("garbagecollector2", new GarbageCollector2());
//        mapping.put("classesloaded", new ClassesLoaded());
//        mapping.put("activemq", new ActiveMQ());
//        mapping.put("deferredmq", new DefferedAMQ());
//        mapping.put("threads", new Threads());
//        mapping.put("openedfiles", new OpenedFiles());
//        mapping.put("action", new Action());
    }
    private ArrayList<String> directives=new ArrayList<>();
    public void addDirective(String s){
        this.directives.add(s);
    }

    public String runJob(){
        String result="";
        String subquery=null;
        String service=null;
        String param=null;
        try {
        if(directives.size()==1){
            System.out.println("testing zabbix");
            ZabbixImitation zi = new ZabbixImitation();
            new Thread(zi).start();
        }else if(directives.size()==3){//3 param
            service=directives.get(0);
            param=directives.get(1);
            subquery=directives.get(2);
            result = String.valueOf(mapping.get(service).get(param, subquery));
        }else if(directives.size()==2){//2 param
            service=directives.get(0);
            param=directives.get(1);
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
}
