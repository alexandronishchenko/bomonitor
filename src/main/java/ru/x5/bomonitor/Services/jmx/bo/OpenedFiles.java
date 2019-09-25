package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.openedfiles.OpenFileDescriptorCount")
@ServiceUnitJMX("Открытые файлы")
public class OpenedFiles extends JMXparrent {

    public OpenedFiles() {
        this.name="openedfiles";
        this.value="java.lang:type=OperatingSystem";
    }
}
