package ru.x5.bomonitor;

import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;
import ru.x5.bomonitor.Services.nativ.*;
import ru.x5.bomonitor.ZQL.JMXservice;
import ru.x5.bomonitor.ZQL.LogService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FullDiag extends ru.x5.bomonitor.ZQL.Service {
    static HashMap<String, String> mappingjmx = new HashMap<>();    static HashMap<String, Service> mapping = new HashMap<>();
    static HashMap<String, ru.x5.bomonitor.ZQL.Service> mappinglog = new HashMap<>();

//    public static void main(String[] args) {
//        getMetric();
//    }
    static {
        mappingjmx.put("activemq", "TotalMessageCount");
        mappingjmx.put("classloaded","LoadedClassCount" );
        mappingjmx.put("defactivemq","QueueSize" );
        mappingjmx.put("gc1", "CollectionCount");
        mappingjmx.put("gc2", "CollectionCount");
        mappingjmx.put("heap", "HeapMemoryUsage.used");
        mappingjmx.put("openedfiles","OpenFileDescriptorCount" );
        mappingjmx.put("threads", "ThreadCount");

//        mapping.put("loyalty", new Loyalty());
//        mapping.put("db", new DBMonitoring());
//        mapping.put("egais", new EGAIS());
//        mapping.put("prices", new Prices());
//        mapping.put("items", new Items());
//        mapping.put("printers", new Printers());
        mapping.put("reciepts", new Reciepts());
//        mapping.put("stock", new Stock());
//        mapping.put("taskmanager", new Taskmanager());
//        mapping.put("transportmodule", new TransportModule());
//        mapping.put("firebird", new Firebird());

        mappinglog.put("boservererror", new LogService());
        mappinglog.put("postgreslog", new LogService());

    }

         @Override
          public String getMetric(){
             return diag();
         }


      private String diag(){
        //return "";
        String result ="";
        result+="Phase 1 (services jmx):\n";
        for(Map.Entry<String,String> pair : mappingjmx.entrySet()){
            ArrayList<String> dirs=new ArrayList<>();
            dirs.add("jmx");
            dirs.add(pair.getKey());
            dirs.add(pair.getValue());

            result+=pair.getKey()+"\n";
            ru.x5.bomonitor.ZQL.Service jmxservice = new JMXservice();
            jmxservice.setDirectives(dirs);
            //pair.getValue();
            //Method[] meth = service.getClass().getDeclaredMethods();
           // System.out.println(service.getClass());
            result+=jmxservice.getMetric()+"\n";
        }
        result+="Phase 2 ( services native):\n";
        for(Map.Entry<String,Service> pair : mapping.entrySet()){
            result+=pair.getValue().getClass().getAnnotation(ServiceUnit.class).value()+"\n";
            Service service = pair.getValue();
            Method[] meth = service.getClass().getDeclaredMethods();
            //System.out.println(service.getClass());
            for(Method m : meth){
                if(m.getAnnotation(Metric.class)!=null){
                    //System.out.println(m.getName());
                    try {
                        result+=m.getAnnotation(Metric.class).value()+": "+m.invoke(service)+"\n";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        System.out.println("smt wrong with method.");
                        e.printStackTrace();
                    }
                }
            }

        }
        result+="Phase 3 (log errors):\n";
        for(Map.Entry<String,ru.x5.bomonitor.ZQL.Service> pair : mappinglog.entrySet()){
            System.out.println(pair.getKey());
            ArrayList<String> dirs=new ArrayList<>();
            dirs.add("log");
            dirs.add(pair.getKey());
            ru.x5.bomonitor.ZQL.Service log = pair.getValue();
            log.setDirectives(dirs);
            result+=log.getMetric()+"\n";

        }

//        System.out.println("fulldiag or fulldiag.db(reciepts ...)");
//        System.out.println("log.bolog or log.postgreslog or log.bolog.error");
//        System.out.println("native.firebird.actual");
//        System.out.println("native.egais.gettmptables");
//        System.out.println("native.items.getdiff");
//        System.out.println("native.db.activerequests");
//        System.out.println("native.db.frozentransaction");
//        System.out.println("native.db.long");
//        System.out.println("native.db.tmptables");
//        System.out.println("native.prices.errorchange");
//        System.out.println("native.printers.queue");
//        System.out.println("native.reciepts.balancediff");
//        System.out.println("native.reciepts.duplicatebon");
//        System.out.println("native.reciepts.incorrectbon");
//        System.out.println("native.reciepts.queue");
//        System.out.println("native.reciepts.stockandreciept");
//        System.out.println("native.stock.geterrors");
//        System.out.println("native.taskmanager.count");
//        System.out.println("native.transportmodule.geterrors");
//        System.out.println("jmx.heap.HeapMemoryUsage.used");
//        System.out.println("jmx.gc1.CollectionCount");
//        System.out.println("jmx.gc2.CollectionCount");
//        System.out.println("jmx.classesloaded.LoadedClassCount");
//        System.out.println("jmx.activemq.TotalMessageCount");
//        System.out.println("jmx.defactivemq.QueueSize");
//        System.out.println("jmx.threads.ThreadCount");
//        System.out.println("jmx.openedfiles.OpenFileDescriptorCount");

        //System.out.println(result);
        return result;
    }
}
