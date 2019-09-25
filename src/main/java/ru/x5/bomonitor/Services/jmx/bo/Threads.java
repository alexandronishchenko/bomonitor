package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.threads.ThreadCount")
@ServiceUnitJMX("Потоки")
public class Threads extends JMXparrent {

    public Threads() {
        this.name="threads";
        this.value="java.lang:type=Threading";
    }
}
