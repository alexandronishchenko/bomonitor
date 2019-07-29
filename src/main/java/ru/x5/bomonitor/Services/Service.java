package ru.x5.bomonitor.Services;


public interface Service {
    int get(String directive);
    int get(String directive,String subquery);
}
