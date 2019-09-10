package ru.x5.bomonitor;


import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.zabbix.ZabbixImitation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

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
        //initialize();//reflection init services
        if(args.length==1){
            System.out.println("testing zabbix");
            logger.insertRecord(bomonitor.class,"Testing zabbix", LogLevel.info);
            ZabbixImitation zi = new ZabbixImitation(Integer.parseInt(properties.getProperty("port")));
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
     * Не используется, возможно будет сканирование на аннотации.
     */
    public static void initialize(){
        ArrayList<File> classes = new ArrayList<>();
        scanDir(String.valueOf(bomonitor.class.getProtectionDomain().getCodeSource().getLocation()),classes);
        for(File f : classes){
            System.out.println(f.getName());
        }

    }

    /**
     * Метод сканирования директории.
     * @param file
     * @param classes
     * @return
     */
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

    /**
     * Выводи все доступные метрики. Опять же, желательно перевести на рефлексию.
     */
    static void printAllMetrics(){
        System.out.println("fulldiag or fulldiag.db(reciepts ...)");
        System.out.println("log.bolog or log.postgreslog or log.bolog.error");
        System.out.println("native.firebird.actual");
        System.out.println("native.egais.gettmptables");
        System.out.println("native.items.getdiff");
        System.out.println("native.db.activerequests");
        System.out.println("native.db.frozentransaction");
        System.out.println("native.db.long");
        System.out.println("native.db.tmptables");
        System.out.println("native.prices.errorchange");
        System.out.println("native.printers.queue");
        System.out.println("native.reciepts.balancediff");
        System.out.println("native.reciepts.duplicatebon");
        System.out.println("native.reciepts.incorrectbon");
        System.out.println("native.reciepts.queue");
        System.out.println("native.reciepts.stockandreciept");
        System.out.println("native.stock.geterrors");
        System.out.println("native.taskmanager.count");
        System.out.println("native.transportmodule.geterrors");
        System.out.println("jmx.heap.HeapMemoryUsage.used");
        System.out.println("jmx.gc1.CollectionCount");
        System.out.println("jmx.gc2.CollectionCount");
        System.out.println("jmx.classesloaded.LoadedClassCount");
        System.out.println("jmx.activemq.TotalMessageCount");
        System.out.println("jmx.defactivemq.QueueSize");
        System.out.println("jmx.threads.ThreadCount");
        System.out.println("jmx.openedfiles.OpenFileDescriptorCount");

    }

    public static Logger getLogger(){
        return logger;
    }

}
