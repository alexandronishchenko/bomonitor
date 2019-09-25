package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.gc1.CollectionCount")
@ServiceUnitJMX("Сборщик мусора 1")
public class GC1 extends JMXparrent {

    public GC1() {
        this.name="gc1";
        this.value="java.lang:type=GarbageCollector,name=PS MarkSweep";
    }
}
