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
import ru.x5.bomonitor.zabbix.ZabbixAgentServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
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
        initializeNativeServices();
        if (args.length == 1 && args[0].equals("daemon")) {
            ArrayList<String> servicesToStart = new ArrayList<>();
            System.out.println("testing zabbix");
            logger.insertRecord(bomonitor.class.getName(), "Testing zabbix", LogLevel.info);
            ZabbixAgentServer zi = new ZabbixAgentServer(Integer.parseInt(properties.getProperty("port")));
            Thread zabbix = new Thread(zi);
            zabbix.start();
            while (true) {
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

}
