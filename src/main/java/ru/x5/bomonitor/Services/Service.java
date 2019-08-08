package ru.x5.bomonitor.Services;


public interface Service {
    String get(String directive);
    String get(String directive,String subquery);
}
