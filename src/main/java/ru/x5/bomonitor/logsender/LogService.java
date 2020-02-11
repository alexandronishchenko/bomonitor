package ru.x5.bomonitor.logsender;


import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.ServiceController;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.logsender.senders.Sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//TODO: cache handler instead different classes, filehandlers, keeper logs.
/**
 * Service control log parsing threads. Singletone like zabbix-service thread. Singletone. @ServiceController
 */
@ServiceController(name="Log monitor controller")
public class LogService implements Runnable{

    private static LogService instance;
    private static boolean run;
    private Logger logger;

    private LogService() {
        logger= bomonitor.getLogger();
        logger.insertRecord(this,"Log monitoring started.", LogLevel.info);
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


    /**
     * Запускает и проверяет состояние потоков чтения и записи. Управляет Sender Saver Reader.
     */
    @Override
    public void run() {
        //TODO: use logsender (pos developed) library service.
//        run=true;
//        List<String> files = Arrays.asList(bomonitor.properties.getProperty("log.files").split(","));
//        List<Thread> logMonitors = new ArrayList<>();
//        files.forEach(file -> {
//            logMonitors.add(new Thread(new LogParseThread(file)));
//        });
//        logMonitors.forEach(monitor -> monitor.start());
//        Sender sender = new Sender();
//        Thread senderThread=new Thread(sender);
//        senderThread.start();
//        while (isRun()){
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            for(Thread th : logMonitors){
//                if(!th.isAlive()){
//                    th.start();
//                }
//            }
//            if(!sender.isRunning()){
//                senderThread.start();
//            }
//
//            try{
//                Thread.currentThread().sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                logger.insertRecord(this,"Thread is dead", LogLevel.error);
//            }
//        }
//        for (Thread th : logMonitors){
//            th.interrupt();
//        }
//        logger.insertRecord(this,"Log service finished.",LogLevel.debug);
    }

}
