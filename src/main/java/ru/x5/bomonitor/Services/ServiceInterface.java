package ru.x5.bomonitor.Services;


public interface ServiceInterface {
    String get(String directive);
    String get(String directive,String subquery);
}
