package ru.x5.bomonitor;


import org.reflections.Reflections;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.jmx.ServiceJMXInterface;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.zabbix.ZabbixAgentServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

/**
 * Класс для запуска - основной.
 */
public class bomonitor {

    public static Properties properties;
    public static Logger logger;
    static {
        properties=new Properties();
        try {
            properties.load(new FileInputStream("/etc/zabbix/bomonitor/bomonitor.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger=new Logger();
    }

    public static void main(String[] args) {
        if(args.length==1){
            System.out.println("testing zabbix");
            logger.insertRecord(bomonitor.class.getName(),"Testing zabbix", LogLevel.info);
            ZabbixAgentServer zi = new ZabbixAgentServer(Integer.parseInt(properties.getProperty("port")));
            Thread zabbix = new Thread(zi);
            zabbix.start();
            while(true){
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!zi.getRun()){
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        logger.insertRecord(bomonitor.class.getName(),"Socket rerun.",LogLevel.info);
                        e.printStackTrace();
                    }
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

    /**
     * Для вызова в других классах для получения всех сервисов. Мапы можно удалять.
     */
    public static HashMap<String, ServiceNativeInterface> initializeNativeServices(){
        HashMap<String, ServiceNativeInterface> classes = new HashMap<>();
        Reflections reflections = new Reflections(bomonitor.class.getPackage().getName());
        Set<Class<?>> classesSet = reflections.getTypesAnnotatedWith(ServiceNative.class);

        for(Class cls: classesSet) {
            Class cl;
            try {
                cl = Class.forName(cls.getName());
                classes.put(cls.getSimpleName().toLowerCase(),(ServiceNativeInterface)cl.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
             catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //ServiceUnit target = (ServiceUnit) cls.getAnnotation(ServiceUnit.class);
        }
        return classes;

    }

    public static HashMap<String, String> initializeJMXServices(){
        HashMap<String,String> classes = new HashMap<>();
        Reflections reflections = new Reflections(bomonitor.class.getPackage().getName());
        Set<Class<?>> classesSet = reflections.getTypesAnnotatedWith(ServiceUnitJMX.class);

        for(Class cls: classesSet) {
            Class cl;
            try {
                cl = Class.forName(cls.getName());
                ServiceJMXInterface inst = (ServiceJMXInterface)cl.newInstance();
                classes.put(inst.getName().toLowerCase(),inst.getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }
        return classes;

    }
    /**
     * Выводи все доступные метрики. Опять же, желательно перевести на рефлексию.
     */
    static ArrayList<String> printAllMetrics(){
        ArrayList<String> info = new ArrayList<>();
        info.add("fulldiag or fulldiag.db(reciepts ...)");
        info.add("log.bolog or log.postgreslog or log.bolog.error");
        info.add("native.firebird.actual");
        info.add("native.egais.gettmptables");
        info.add("native.items.getdiff");
        info.add("native.db.activerequests");
        info.add("native.db.frozentransaction");
        info.add("native.db.long");
        info.add("native.db.tmptables");
        info.add("native.prices.errorchange");
        info.add("native.printers.queue");
        info.add("native.reciepts.balancediff");
        info.add("native.reciepts.duplicatebon");
        info.add("native.reciepts.incorrectbon");
        info.add("native.reciepts.queue");
        info.add("native.reciepts.stockandreciept");
        info.add("native.stock.geterrors");
        info.add("native.taskmanager.count");
        info.add("native.transportmodule.geterrors");
        info.add("jmx.heap.HeapMemoryUsage.used");
        info.add("jmx.gc1.CollectionCount");
        info.add("jmx.gc2.CollectionCount");
        info.add("jmx.classesloaded.LoadedClassCount");
        info.add("jmx.activemq.TotalMessageCount");
        info.add("jmx.defactivemq.QueueSize");
        info.add("jmx.threads.ThreadCount");
        info.add("jmx.openedfiles.OpenFileDescriptorCount");
        for (int i = 0; i < info.size(); i++) {
            System.out.println(info.get(i));
        }
        return info;

    }

    public static Logger getLogger(){
        return logger;
    }

}
