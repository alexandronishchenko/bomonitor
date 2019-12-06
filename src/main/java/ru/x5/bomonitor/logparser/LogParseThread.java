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
import java.util.Arrays;
import java.util.List;

public class LogParseThread implements Runnable, LogMonitor {
    private List<Consumer> consumers;
    private volatile boolean running;
    private Cache cache;
    private CachedRecordEntity recordEntity;
    private File logFile;
    private FileInputStream fileInputStream;
    private Logger logger = bomonitor.getLogger();
    private FileChannel fc = null;

    public LogParseThread(String logFile) {
        this.logFile = new File(logFile);
        //if(logFile.e)
        try {
            fileInputStream = new FileInputStream(logFile);
        } catch (FileNotFoundException e) {
            logger.insertRecord(this, "Log file does not exist." + this.logFile.getAbsolutePath(), LogLevel.warn);
            e.printStackTrace();
        }
        logger.insertRecord(this, "Parsing of log: " + this.logFile.getAbsolutePath() + " started.", LogLevel.info);
        this.cache = Cache.getInstance();
        recordEntity = cache.getRecordForFile(this.logFile);
        List<String> consList= Arrays.asList(bomonitor.properties.getProperty("log.consumer").split(","));
        consList.forEach(consumer -> this.consumers.add(new Consumer(consumer)));
        this.running = true;
    }

    @Override
    public void run() {
//TODO: Проверека - тот же файл или нет. Нет - обновить кэш.
        //fileInputStream.getChannel().position();
        while (running) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int i = 0;
            long pos;
            char c;
            fc = fileInputStream.getChannel();
            try {
                fileInputStream.getChannel().position(this.recordEntity.getFilePosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                StringBuilder sb = new StringBuilder();
                while ((i = fileInputStream.read()) != -1) {

                    // get channel position
                    this.recordEntity.filePosition = fc.position();

                    // integer to character
                    c = (char) i;
                    if (c == '\r' || c == '\n') {
                        sendLine(sb.toString());
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
        this.consumers.forEach(consumer -> sendLine(line));
    }

    //Service methods
    public boolean isRunning() {
        return running;
    }
}
