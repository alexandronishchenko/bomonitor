package ru.x5.bomonitor.ZQL;

import ru.x5.bomonitor.FullDiag;

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
                //job = new FullDiag();
                break;
            case "hw":
                job = new HardWareService();
                break;
        }
        try {
            job.setDirectives(directives);
        }catch (NullPointerException e){
            System.out.println("No such metric.");
        }
        String res="";
        try {
            res = job.getMetric();
        }catch (NullPointerException e){

        }
        return res;
    }
    /*
    jmx|RealNameOfBean|RealNameAttribute
    native|queryConst&queryConst2&queryConst3|sum
     */

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
