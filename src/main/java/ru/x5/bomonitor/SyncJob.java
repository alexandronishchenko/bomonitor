package ru.x5.bomonitor;


import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.zabbix.ZabbixAgentServer;


import java.util.ArrayList;
import java.util.HashMap;

public class SyncJob {

    static HashMap<String, ServiceNativeInterface> mapping = bomonitor.initializeNativeServices();

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
            ZabbixAgentServer zi = new ZabbixAgentServer();
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

