package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import ru.x5.bomonitor.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Job implements Runnable {

    static HashMap<String, Service> mapping = new HashMap<>();
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
    private ArrayList<String> directives=new ArrayList<>();
    private Boolean isRun;

    public Job() {
        this.isRun=false;
    }
    public Job(Boolean b) {
        this.isRun=b;
    }

    public void run() {
//        while(isRun){
//
//        }


    }
    public void addDirective(String s){
        this.directives.add(0,s);
    }
    public void setRun(Boolean b){
        this.isRun=b;
    }

    public int runJob(){

        int result=0;
        String subquery=null;
        String service=null;
        String param=null;
        if(directives.size()==1){
            System.out.println("testing zabbix");
            ZabbixImitation zi = new ZabbixImitation();
            new Thread(zi).start();
        }else if(directives.size()==3){
            service=directives.get(0);
            param=directives.get(1);
            subquery=directives.get(2);
            result=mapping.get(service).get(param,subquery);
        }else if(directives.size()==2){
            service=directives.get(0);
            param=directives.get(1);
            result=mapping.get(service).get(param);
        }else {
            System.out.println("incorrect param");
        }
        return result;
    }
}

