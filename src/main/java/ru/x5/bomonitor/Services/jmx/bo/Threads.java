package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;

@ServiceUnitJMX("Потоки")
public class Threads extends JMXparrent {

    public Threads() {
        this.name="threads";
        this.value="java.lang:type=Threading";
    }
}
