package ru.x5.bomonitor.Services.jmx;

import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

@ServiceUnit
public class Heap extends JMXService implements Service {
    String name = "java.lang:type=Memory";

    @Override
    public int get(String directive) {
        long result =0;


        return (int)result;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    }

    @Override
    public int get(String directive, String subquery) {
        long result =0;

//        try {
//           // result=jmXconnector.docon(name,directive,subquery);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (MalformedObjectNameException e) {
//            e.printStackTrace();
//        } catch (InstanceNotFoundException e) {
//            e.printStackTrace();
//        } catch (MBeanException e) {
//            e.printStackTrace();
//        } catch (AttributeNotFoundException e) {
//            e.printStackTrace();
//        } catch (ReflectionException e) {
//            e.printStackTrace();
//        }
        return (int)result;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
    }
}