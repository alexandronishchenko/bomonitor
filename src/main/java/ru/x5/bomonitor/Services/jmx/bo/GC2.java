package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;

@ServiceUnitJMX("Сборщик мусора 2")
public class GC2 extends JMXparrent {

    public GC2(){
        this.name="gc2";
        this.value="java.lang:type=GarbageCollector,name=PS Scavenge";
    }
}
