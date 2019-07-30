package ru.x5.bomonitor.ZQL;

import ru.x5.bomonitor.JMXclient.JMXconnector;

import javax.management.*;
import java.io.IOException;
import java.util.Arrays;

public class JMXservice extends Service {
    protected JMXconnector jmXconnector= new JMXconnector();
    private String name;
@Override
public String getMetric(){
        long result=0;
        String[] params = (String[]) this.directives.toArray();
        String[] pr = Arrays.copyOfRange(params,1,directives.size());
        this.name=directives.get(0);
        try {
            result=jmXconnector.docon(this.name,pr);
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
        }
        return String.valueOf(result);
    }

}
