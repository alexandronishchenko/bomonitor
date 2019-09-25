package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.heap.HeapMemoryUsage.used")
@ServiceUnitJMX("Куча")
public class Heap extends JMXparrent {


    public Heap() {
        this.name="heap";
        this.value= "java.lang:type=Memory";
    }
}
