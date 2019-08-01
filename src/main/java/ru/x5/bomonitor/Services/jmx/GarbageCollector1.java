package ru.x5.bomonitor.Services.jmx;

import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

@ServiceUnit("GC1")
public class GarbageCollector1 extends JMXService implements Service {
    String name="java.lang:type=GarbageCollector,name=PS MarkSweep";
    @Override
    public int get(String directive) {
        long result= 0;
//        try {
//            result=jmXconnector.docon(name,directive);
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

        return (int)result;

    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }
}
