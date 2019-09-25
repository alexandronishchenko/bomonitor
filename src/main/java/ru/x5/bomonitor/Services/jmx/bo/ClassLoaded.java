package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;

@ServiceUnitJMX("Загруженные классы")
public class ClassLoaded extends JMXparrent {

    public ClassLoaded() {
        this.name="classloaded";
        this.value="java.lang:type=ClassLoading";
    }
}
