package ru.x5.bomonitor;


import org.reflections.Reflections;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.Services.ZQL.Composer;
import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceJMXInterface;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.logparser.LogService;
import ru.x5.bomonitor.zabbixagentimpl.ZabbixAgentServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Класс для запуска - основной.
 */
public class bomonitor {

    public static Properties properties;
    public static Logger logger;
    public static volatile boolean run;

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/zabbix/bomonitor/bomonitor.properties"));
            System.getProperties().setProperty("project", properties.getProperty("project"));//установка сети магазинов для вариабильности работы.
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger = new Logger();
    }

    public static void main(String[] args) {
        run=true;
        initializeNativeServices();
        if (args.length == 1 && args[0].equals("daemon")) {
            List<String> servicesToStart = new ArrayList<>();
            servicesToStart= Arrays.asList(properties.getProperty("services").split(","));
            //zabbixagentimpl,zabbixagentcli,logparse,jmxcli
            //jmxcli checking at Composer.
            //zabbixagentcli checking at Composer befor creating instance.
            //other checks here.
            System.out.println("testing bomonitor daemon");
            logger.insertRecord(bomonitor.class.getName(), "Testing bomonitor daemon", LogLevel.info);


            while (isRun()) {
                checkServices(servicesToStart);
            }


        } else if (args.length == 0) {
            System.out.println("incorrect param");
            printAllMetrics();
        } else {
            //old paradigm with parameters script call.
            System.out.println("run with old paradigm. SOUT results. To run zabbix and log run with @daemon@ param.");
            Composer composer = new Composer(args[0]);
            String result = String.valueOf(composer.getResult());
            if (result.equals("null")) {
                logger.insertRecord(bomonitor.class, "Null result. Sending empty row.", LogLevel.debug);
                result = "";
            }
            System.out.println(result);
        }
        run=false;
    }

    /**
     * Для вызова в других классах для получения всех сервисов. Мапы можно удалять.
     */
    public static HashMap<String, ServiceNativeInterface> initializeNativeServices() {
        HashMap<String, ServiceNativeInterface> classes = new HashMap<>();
        Reflections reflections = new Reflections(bomonitor.class.getPackage().getName());
        Set<Class<?>> classesSet = reflections.getTypesAnnotatedWith(ServiceNative.class);

        for (Class cls : classesSet) {
            Class cl;
            try {
                cl = Class.forName(cls.getName());
                ServiceNativeInterface serv = (ServiceNativeInterface) cl.newInstance();
                classes.put(serv.getName(), serv);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //ServiceUnit target = (ServiceUnit) cls.getAnnotation(ServiceUnit.class);
        }
        return classes;

    }

    public static HashMap<String, String> initializeJMXServices() {
        HashMap<String, String> classes = new HashMap<>();
        Reflections reflections = new Reflections(bomonitor.class.getPackage().getName());
        Set<Class<?>> classesSet = reflections.getTypesAnnotatedWith(ServiceUnitJMX.class);

        for (Class cls : classesSet) {
            Class cl;
            try {
                cl = Class.forName(cls.getName());
                ServiceJMXInterface inst = (ServiceJMXInterface) cl.newInstance();
                classes.put(inst.getName().toLowerCase(), inst.getValue());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
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
    static void printAllMetrics() {
        Reflections reflections = new Reflections(bomonitor.class.getPackage().getName());
        Set<Class<?>> classesSet = reflections.getTypesAnnotatedWith(ServiceNative.class);
        Set<Class<?>> classesSetJMX = reflections.getTypesAnnotatedWith(ServiceUnitJMX.class);

        for (Class cls : classesSet) {
            // try {
            System.out.println(cls.getAnnotation(ServiceNative.class));
            Method[] methods = cls.getMethods();
            for (Method m : methods) {
                if (m.getAnnotation(Metric.class) != null) {
                    System.out.println(m.getAnnotation(Metric.class).value() + " directive: " + m.getAnnotation(Metric.class).directive());

                } else if (m.getAnnotation(StringMetric.class) != null) {
                    System.out.println(m.getAnnotation(StringMetric.class).value() + " directive: " + m.getAnnotation(StringMetric.class).directive());
                }
            }
        }
        for (Class cls : classesSetJMX) {
            System.out.println(cls.getAnnotation(ServiceUnitJMX.class));
            System.out.println(cls.getAnnotation(ZabbixRequest.class).toString());
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static boolean isRun(){
        return run;
    }

    private static void  checkServices(List<String> servicesToStart){
        ZabbixAgentServer zi=null;
        Thread zabbix=null;
        if(servicesToStart.contains("zabbixagentimpl")){
            zi = new ZabbixAgentServer(Integer.parseInt(properties.getProperty("port")));
            zabbix = new Thread(zi);
            zabbix.start();
        }
        LogService ls = null;
        Thread logservice=null;
        if(servicesToStart.contains("logparse")){
            ls= LogService.getInstance();
            logservice = new Thread(ls);
            logservice.start();
        }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!zi.getRun()) {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    logger.insertRecord(bomonitor.class.getName(), "Socket rerun.", LogLevel.info);
                    e.printStackTrace();
                }
                zabbix.start();
            }
            if (!ls.isRun()) {
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    logger.insertRecord(bomonitor.class.getName(), "Socket rerun.", LogLevel.info);
                    e.printStackTrace();
                }
                logservice.start();
            }

    }
}
