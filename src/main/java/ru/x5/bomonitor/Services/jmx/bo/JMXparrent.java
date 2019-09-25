package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.jmx.ServiceJMXInterface;

public class JMXparrent  implements ServiceJMXInterface {
    protected String name;
    protected String value;

    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }

}
