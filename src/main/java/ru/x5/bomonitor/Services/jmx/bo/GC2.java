package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.gc2.CollectionCount")
@ServiceUnitJMX("Сборщик мусора 2")
public class GC2 extends JMXparrent {

    public GC2(){
        this.name="gc2";
        this.value="java.lang:type=GarbageCollector,name=PS Scavenge";
    }
}
