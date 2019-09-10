package ru.x5.bomonitor.Services.ZQL;

import ru.x5.bomonitor.JMXclient.JMXconnector;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

import javax.management.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Класс родитель для JMXconnector
 * @see JMXconnector
 * Содержит карту сервисов JMX. Необходим перевод на рефлексию.
 */
public class JMXservice extends Service {
    /**
     * Непосредственный коннектор с обработчиками.
     */
    private static Logger loger= bomonitor.getLogger();
    protected JMXconnector jmXconnector;//= new JMXconnector();
   // private String name;
    /**
     * Карта сервисов
     */
    public static HashMap<String, String> mapping = new HashMap<>();
    static {
        mapping.put("activemq", "org.apache.activemq:type=Broker,brokerName=brokerActiveMQ");
        mapping.put("classloaded","java.lang:type=ClassLoading" );
        mapping.put("defactivemq","org.apache.activemq:type=Broker,brokerName=brokerActiveMQ,destinationType=Queue,destinationName=sappi.deferred.export" );
        mapping.put("gc1", "java.lang:type=GarbageCollector,name=PS MarkSweep");
        mapping.put("gc2", "java.lang:type=GarbageCollector,name=PS Scavenge");
        mapping.put("heap", "java.lang:type=Memory");
        mapping.put("openedfiles","java.lang:type=OperatingSystem" );
        mapping.put("threads", "java.lang:type=Threading");
    }

    /**
     * Запрос метрики от сервера.
     * @return строковый результат запроса или пустую строку.
     */
    @Override
public String getMetric(){
        long result=0;
        try {

            List<String> pr = directives.subList(2,directives.size());
            String[] params = new String[pr.size()];
            for (int i = 0; i < pr.size(); i++) {
                params[i]=pr.get(i);
            }
            String system = directives.get(2).toUpperCase();
            if(system.contains("POS")){
                if(bomonitor.properties.getProperty("pos_monitoring").equals("false")){
                    loger.insertRecord(this,"POS monitoring is disabled at config.",LogLevel.error);
                    return null;
                }
                jmXconnector=new JMXconnector(system+":18012");
            }else if(system.equals("BO")){
                jmXconnector= new JMXconnector("localhost:6422");
            }else{
                loger.insertRecord(this,"Wrong destination system POS or BO",LogLevel.error);
            }
            result=jmXconnector.docon(JMXservice.mapping.get(directives.get(1)),params);
            jmXconnector=null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
        } catch (MBeanException e) {
            e.printStackTrace();
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        } catch (ReflectionException e) {
            e.printStackTrace();
        }catch (ClassCastException e){
            loger.insertRecord(this,"cast error", LogLevel.error);
        }
        return String.valueOf(result);
    }

}
