package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;

@ServiceUnitJMX("Куча")
public class Heap extends JMXparrent {


    public Heap() {
        this.name="heap";
        this.value= "java.lang:type=Memory";
    }
}
