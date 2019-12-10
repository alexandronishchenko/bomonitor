package ru.x5.bomonitor.logparser;


import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.ServiceController;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.logparser.senders.Sender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service control log parsing threads. Singletone like zabbix-service thread.
 */
@ServiceController(name="Log monitor controller")
public class LogService implements Runnable{

    private static LogService instance;
    private static boolean run;
    private Logger logger;

    private LogService() {
        logger= bomonitor.getLogger();
        logger.insertRecord(this,"Log monitoring started.", LogLevel.debug);
    }
    public static LogService getInstance(){
        if(instance==null){
            instance = new LogService();
        }
        return instance;
    }

    public static boolean isRun(){
        return LogService.run;
    }


    @Override
    public void run() {
        run=true;
        List<String> files = Arrays.asList(bomonitor.properties.getProperty("log.files").split(","));
        List<Thread> logMonitors = new ArrayList<>();
        files.forEach(file -> {
            logMonitors.add(new Thread(new LogParseThread(file)));
        });
        logMonitors.forEach(monitor -> monitor.start());
        Thread senderThread=new Thread(new Sender());
        senderThread.start();
        while (isRun()){
            for(Thread th : logMonitors){
                if(!th.isAlive()){
                    th.start();
                }
            }
            if(!senderThread.isAlive()){
                senderThread.start();
            }

            try{
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread th : logMonitors){
            th.interrupt();
        }
        logger.insertRecord(this,"Log service finished.",LogLevel.debug);
    }

}
