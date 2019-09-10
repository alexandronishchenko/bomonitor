package ru.x5.bomonitor.JMXclient;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Services.ZQL.JMXservice;
import ru.x5.bomonitor.bomonitor;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

/**
 * Класс используется для непосредственного открытия и закрытия коннектора к JMX-серверу приложения.
 */

public class JMXconnector {
    private String HOST;
    public JMXconnector(String host){
        this.HOST=host;
    }
    /**
     * Метод открывает соединение и обрабатывает полученный результат.
     * @param name имя в хэшмэп класса-родителя
     * @param param параметры, которые необходимо получить в формате JMX ,  то есть их можно получить непосредственно из JMC. Поддерживает как обращение к бинам, так и к непосредственному параметру.
     *              единственное ограничение сопоставление в родительском классе. Лучшее решение добавить рефлексию для автоинициализации классов с аннотацией @service
     * @see @Service
     * @return возвращет строку со значением от сервера
     * @throws IOException
     * @throws MalformedObjectNameException
     * @throws InstanceNotFoundException
     * @throws MBeanException
     * @throws AttributeNotFoundException
     * @throws ReflectionException
     */
    public long docon(String name,String[] param) throws IOException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, AttributeNotFoundException, ReflectionException {
        //Create an RMI connector client and connect it to the RMI connector server
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+this.HOST+"/jmxrmi");//!!!!!!!!!!
        JMXConnector jmxc=null;
        long result=0;
        try {
            jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            result= Long.parseLong(getData(mbsc,name,param));
            //jmxc.close();
            return result;
        }catch (IOException e){
            bomonitor.getLogger().insertRecord(this,"JMX unavailable.", LogLevel.warn);
        }finally {
            try {
                jmxc.close();
            }catch (NullPointerException e){
                bomonitor.getLogger().insertRecord(this,"there is no JMX connector", LogLevel.info);
            }
        }
        return result;

    }

    /**
     *
     * @param mbsc коннектор, с которого будем получать данные
     * @param name имя параметра
     * @param param вариативный массив для запроса или с бин-сервера или же уже с объекта.
     * @return Строку данных от сервера.
     * @throws MalformedObjectNameException
     * @throws AttributeNotFoundException
     * @throws MBeanException
     * @throws ReflectionException
     * @throws InstanceNotFoundException
     * @throws IOException
     */
    private String getData(MBeanServerConnection mbsc,String name, String[] param) throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException, IOException {
        String result="";
        if(param.length==1) {
            Object sd = mbsc.getAttribute(new ObjectName(name), param[0]);
            result=sd.toString();//(composite.get(param)).toString();
        }else if(param.length==2){
            CompositeData composite =
                    (CompositeData)mbsc.getAttribute(new ObjectName(name),
                            param[0]);
            result=(composite.get(param[1])).toString();
        }
        //System.out.println(result);
        return result;
    }
}
