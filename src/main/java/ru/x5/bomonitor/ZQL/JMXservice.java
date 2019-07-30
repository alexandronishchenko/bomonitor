package ru.x5.bomonitor.ZQL;

import ru.x5.bomonitor.JMXclient.JMXconnector;
import ru.x5.bomonitor.Services.nativ.*;

import javax.management.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JMXservice extends Service {
    protected JMXconnector jmXconnector= new JMXconnector();
   // private String name;
    static HashMap<String, String> mapping = new HashMap<>();
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
@Override
public String getMetric(){
        long result=0;
        try {

            List<String> pr = directives.subList(2,directives.size());
            String[] params = new String[pr.size()];
            for (int i = 0; i < pr.size(); i++) {
                params[i]=pr.get(i);
            }
            result=jmXconnector.docon(JMXservice.mapping.get(directives.get(1)),params);
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
            System.out.println("cast error");
        }
        return String.valueOf(result);
    }

}
