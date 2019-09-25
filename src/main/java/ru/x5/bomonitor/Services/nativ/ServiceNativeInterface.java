package ru.x5.bomonitor.Services.nativ;


public interface ServiceNativeInterface {
    public String getName();
    public String getValue();
    String get(String directive);
    String get(String directive,String subquery);
}
