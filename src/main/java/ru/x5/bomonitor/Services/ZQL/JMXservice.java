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
    public static HashMap<String, String> mapping = bomonitor.initializeJMXServices();

    /**
     * Запрос метрики от сервера.
     * @return строковый результат запроса или пустую строку.
     */
    @Override
public String getMetric(){
        long result=0;
        try {

            List<String> pr = directives.subList(3,directives.size());
            String[] params = new String[pr.size()];
            for (int i = 0; i < pr.size(); i++) {
                params[i]=pr.get(i);
            }
            String system = directives.get(1).toUpperCase();
            loger.insertRecord(this,"System was fetched to monitor:"+system,LogLevel.debug);
            if(system.contains("POS")){
                if(bomonitor.properties.getProperty("pos_monitoring").equals("false")){
                    loger.insertRecord(this,"POS monitoring is disabled at config.",LogLevel.error);
                    return null;
                }

                jmXconnector=new JMXconnector(system,bomonitor.properties.getProperty("JMX_port_pos"));
            }else if(system.equals("BO")){
                jmXconnector= new JMXconnector("127.0.0.2",bomonitor.properties.getProperty("JMX_port_bo"));
            }else{
                loger.insertRecord(this,"Wrong destination system POS or BO",LogLevel.error);
            }
            result=jmXconnector.docon(JMXservice.mapping.get(directives.get(2)),params);
            loger.insertRecord(this,"JMX result is: "+result,LogLevel.debug);
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
            e.printStackTrace();
        }
        return String.valueOf(result);
    }

}
