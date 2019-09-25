package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.classloaded.LoadedClassCount")
@ServiceUnitJMX("Загруженные классы")
public class ClassLoaded extends JMXparrent {

    public ClassLoaded() {
        this.name="classloaded";
        this.value="java.lang:type=ClassLoading";
    }
}
