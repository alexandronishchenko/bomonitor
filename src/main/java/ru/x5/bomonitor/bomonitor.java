package ru.x5.bomonitor;

import ru.x5.bomonitor.JMXclient.JMXconnector;
import ru.x5.bomonitor.Services.*;
import ru.x5.bomonitor.ru.x5.bomonitor.threading.ZabbixImitation;

import javax.management.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class bomonitor {
//    static Properties properties = new Properties();
    static HashMap<String,Service> mapping = new HashMap<>();
    static Properties properties;
    static {
        //"/etc/zabbix/agentjar.properties"
        properties=new Properties();
        try {
            properties.load(new FileInputStream("/etc/zabbix/agentjar.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mapping.put("heap", new Heap());
        mapping.put("garbagecollector", new GarbageCollector1());
        mapping.put("garbagecollector2", new GarbageCollector2());
        mapping.put("classesloaded", new ClassesLoaded());
        mapping.put("activemq", new ActiveMQ());
        mapping.put("deferredmq", new DefferedAMQ());
        mapping.put("threads", new Threads());
        mapping.put("openedfiles", new OpenedFiles());
        mapping.put("action", new Action());
    }

    public static void main(String[] args) {
        //initialize();//reflection init services
        String subquery=null;
        String service=null;
        String param=null;
        if(args.length==1){
            System.out.println("testing zabbix");
            ZabbixImitation zi = new ZabbixImitation(Integer.parseInt(properties.getProperty("port")));
            new Thread(zi).start();
        }else if(args.length==3){
            service=args[0];
            param=args[1];
            subquery=args[2];
            System.out.println(String.valueOf(mapping.get(service).get(param,subquery)));
        }else if(args.length==2){
            service=args[0];
            param=args[1];
            System.out.println(String.valueOf(mapping.get(service).get(param)));
        }else {
            System.out.println("incorrect param");
            printAllMetrics();
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
