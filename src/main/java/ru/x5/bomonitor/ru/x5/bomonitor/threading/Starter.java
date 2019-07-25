package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import ru.x5.bomonitor.ru.x5.bomonitor.threading.Job;

public class Starter {
    public static void runJob(Job j){
        new Thread(j).start();
    }
}
