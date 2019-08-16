package ru.x5.bomonitor.JMXclient;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.bomonitor;

import javax.management.*;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;


public class JMXconnector {

    public long docon(String name,String[] param) throws IOException, MalformedObjectNameException, InstanceNotFoundException, MBeanException, AttributeNotFoundException, ReflectionException {
        //Create an RMI connector client and connect it to the RMI connector server
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:6422/jmxrmi");//!!!!!!!!!!
        JMXConnector jmxc=null;
        long result=0;
        try {
            jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            result= Long.parseLong(getData(mbsc,name,param));
            //jmxc.close();
            return result;
        }catch (IOException e){
            bomonitor.getLogger().insertRecord("JMX unavailable.", LogLevel.warn);
        }finally {
            try {
                jmxc.close();
            }catch (NullPointerException e){
                bomonitor.getLogger().insertRecord("there is no JMX connector", LogLevel.info);
            }
        }
        return result;

    }

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
