package ru.x5.bomonitor;


import ru.x5.bomonitor.ru.x5.bomonitor.threading.SyncJob;
import ru.x5.bomonitor.ru.x5.bomonitor.threading.ZabbixImitation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class bomonitor {

    static Properties properties;
    static {
        properties=new Properties();
        try {
            properties.load(new FileInputStream("/etc/zabbix/agentjar.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //initialize();//reflection init services
        if(args.length==1){
            System.out.println("testing zabbix");
            ZabbixImitation zi = new ZabbixImitation(Integer.parseInt(properties.getProperty("port")));
            Thread zabbix = new Thread(zi);
            zabbix.start();
            while(true){
                if(!zi.getRun()){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zi=new ZabbixImitation(Integer.parseInt(properties.getProperty("port")));
                    zabbix = new Thread(zi);
                    zabbix.start();
                }
            }


        }else if(args.length==0){
            System.out.println("incorrect param");
            printAllMetrics();
        }
        else{
            SyncJob job = new SyncJob();
            for (int i = 0; i < args.length; i++) {
                job.addDirective(args[i]);
            }
            System.out.println(job.runJob());
        }
    }

    public static void initialize(){
        ArrayList<File> classes = new ArrayList<>();
        scanDir(String.valueOf(bomonitor.class.getProtectionDomain().getCodeSource().getLocation()),classes);
        for(File f : classes){
            System.out.println(f.getName());
        }

    }

    private static ArrayList<File> scanDir(String file,ArrayList<File> classes){
        File fl = new File(file);
        File[] files = fl.listFiles();
        for(File s : files){
            System.out.println(s);
            if(fl.isDirectory()){
                scanDir(s.getName(),classes);
            }else {
                if(fl.getName().contains("class")){
                    classes.add(fl);
                }
            }
        }
        return classes;
    }

    static void printAllMetrics(){
        System.out.println("egais.gettmptables");
        System.out.println("items.getdiff");
        System.out.println("db.activerequests");
        System.out.println("db.frozentransaction");
        System.out.println("db.long");
        System.out.println("db.tmptables");
        System.out.println("prices.errorchange");
        System.out.println("printers.queue");
        System.out.println("reciepts.balancediff");
        System.out.println("reciepts.duplicatebon");
        System.out.println("reciepts.incorrectbon");
        System.out.println("reciepts.queue");
        System.out.println("reciepts.stockandreciept");
        System.out.println("stock.geterrors");
        System.out.println("taskmanager.count");
        System.out.println("transportmodule.geterrors");
        System.out.println("heap.HeapMemoryUsage.used");
        System.out.println("garbagecollector.CollectionCount");
        System.out.println("garbagecollector2.CollectionCount");
        System.out.println("classesloaded.LoadedClassCount");
        System.out.println("activemq.TotalMessageCount");
        System.out.println("deferredmq.QueueSize");
        System.out.println("threads.ThreadCount");
        System.out.println("openedfiles.OpenFileDescriptorCount");

    }

}
