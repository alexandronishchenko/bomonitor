package ru.x5.bomonitor.Services.ZQL;

import ru.x5.bomonitor.Services.nativ.bo.FullDiag;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.zabbix.ZabbixProxing;

import java.io.IOException;
import java.util.ArrayList;

public class Composer {
    private static Logger loger=bomonitor.getLogger();
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
            loger.insertRecord(this,"No job was setted:"+directives.toString(), LogLevel.warn);
        }
        String res=null;
        try {
            res = job.getMetric();
        }catch (NullPointerException e){
            loger.insertRecord(this,"No integrated job was seted, try to resend to zabbix-agent:"+directives.toString(), LogLevel.info);
            try{
                if(!directives.isEmpty()) {
                    ZabbixProxing zp = new ZabbixProxing();
                    String zabRes=zp.sendRequestToClient(comand);
                    System.out.println(zabRes);
                    res=zabRes;
                    loger.insertRecord(this,"Succed send. Result='"+res+"'", LogLevel.debug);
                }
            }catch (IOException g){
                loger.insertRecord(this,"Zabbix to client resend failed.",LogLevel.error);
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
