package ru.x5.bomonitor.Services.nativ;


public interface ServiceNativeInterface {
    String get(String directive);
    String get(String directive,String subquery);
}
