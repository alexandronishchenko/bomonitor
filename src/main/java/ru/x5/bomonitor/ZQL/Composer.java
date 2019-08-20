package ru.x5.bomonitor.ZQL;

import ru.x5.bomonitor.FullDiag;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.ru.x5.bomonitor.threading.ZabbixProxing;

import java.io.IOException;
import java.util.ArrayList;

public class Composer {
    String comand;
    ArrayList<String> directives=new ArrayList<>();
    Service job;
    public Composer(String comand) {
        this.comand = comand;
    }

    public String getResult(){
        this.directives=getServiceParams(this.comand);
        String serviceKind = this.directives.get(0);
        switch (serviceKind){
            case "jmx":
                job = new JMXservice();
                break;
            case "native":
                job = new NativeService();
                break;
            case "log":
                job = new LogService();
                break;
            case "fulldiag":
                job = new FullDiag();
                break;
            case "hw":
                job = new HardWareService();
                break;
        }
        try {
            job.setDirectives(directives);
        }catch (NullPointerException e){
            bomonitor.getLogger().insertRecord("No such metric:"+directives.toString(), LogLevel.error);
        }
        String res="";
        try {
            res = job.getMetric();
        }catch (NullPointerException e){
            bomonitor.getLogger().insertRecord("No such metric:", LogLevel.info);
            try{
                if(!directives.isEmpty()) {
                    ZabbixProxing zp = new ZabbixProxing();
                    System.out.println(zp.sendRequestToClient("system.cpu.load[percpu,avg1]+\n"));
                }
            }catch (IOException g){
                System.out.println("Zabbix to client resend failed.");
            }
        }
        return res;
    }

    ArrayList<String> getServiceParams(String s){
        System.out.println(s);
        ArrayList<String> result=new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i)=='.'){
                result.add(s.substring(0,i));
                s=s.substring(i+1);
                i=0;
            }
        }
        result.add(s);
        return result;
    }
}
