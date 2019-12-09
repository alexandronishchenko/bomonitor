package ru.x5.bomonitor.logparser;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.logparser.consumers.Consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LogParseThread implements Runnable, LogMonitor {
    private List<Consumer> consumers;
    private volatile boolean running;
    private Cache cache;
    private CachedRecordEntity recordEntity;
    private File logFile;
    private FileTime createdTime;
    private FileInputStream fileInputStream;
    private Logger logger = bomonitor.getLogger();
    private FileChannel fc = null;

    public LogParseThread(String logFile) {
        this.logFile = new File(logFile);
        BasicFileAttributes fileAttributes = null;
        try {
            fileAttributes = Files.readAttributes(Paths.get(logFile), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cant load time attributes.");
        }
        List<String> consList= Arrays.asList(bomonitor.properties.getProperty("log.consumer").split(","));
        this.consumers=new ArrayList<>();
        consList.forEach(consumer -> this.consumers.add(new Consumer(consumer)));
        createdTime= fileAttributes.creationTime();
        this.running = true;
        logger.insertRecord(this, "Parsing of log: " + this.logFile.getAbsolutePath() + " started.", LogLevel.info);
        this.cache = Cache.getInstance();
        recordEntity = cache.getRecordForFile(this.logFile);


    }

    @Override
    public void run() {
        while (running) {
//            if(!isSameFile()){
//                this.recordEntity.setFilePosition(0);
//                cache.cacheRecord();
//                continue;
//            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream = new FileInputStream(logFile);
                fc = fileInputStream.getChannel();
            } catch (FileNotFoundException e) {
                logger.insertRecord(this, "Log file does not exist." + this.logFile.getAbsolutePath(), LogLevel.warn);
                e.printStackTrace();
            }
            int i = 0;
            long pos;
            char c;
            try {
                fileInputStream.getChannel().position(this.recordEntity.getFilePosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                StringBuilder sb = new StringBuilder();
                while ((i = fileInputStream.read()) != -1) {
                    //Если файл поменялся - прерываем цикл и открываем все заново.
//                    if(!isSameFile()){
//                        break;
//                    }

                    // get channel position
                    this.recordEntity.setFilePosition(fc.position());

                    // integer to character
                    c = (char) i;
                    if (c == '\r' || c == '\n') {
                        sendLine(sb.toString());
                        cache.cacheRecord();
                        sb = new StringBuilder();
                    } else {
                        sb.append(c);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendLine(String line) {
        System.out.println(line);
        //this.consumers.forEach(consumer -> sendLine(line));
    }

    private boolean isSameFile(){
        boolean same=false;
        BasicFileAttributes cur = null;
        try {
            cur = Files.readAttributes(Paths.get(this.logFile.getAbsolutePath()), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cant load time attributes.");
        }
        if(createdTime.equals(cur.creationTime()))same=true;
        return same;
    }

    private void getActualVars(){

    }
    //Service methods
    public boolean isRunning() {
        return running;
    }
}
