package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;

public class ParrentNativeService implements ServiceNativeInterface {
    protected String name;
    protected String value;
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String get(String directive) {
        return null;
    }

    @Override
    public String get(String directive, String subquery) {
        return null;
    }
}
