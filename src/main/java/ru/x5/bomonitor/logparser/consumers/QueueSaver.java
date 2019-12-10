package ru.x5.bomonitor.logparser.consumers;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QueueSaver {


    public void putLine(String line){
          saveLine(line);
    }

    private void saveLine(String line){
        System.out.println("Saving: " + line);
        File logFile = getHistoryFile();
        try {
            FileWriter writer = new FileWriter(logFile,true);
            writer.write(line+"\r");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private File getHistoryFile(){
        File currentHistory = new File(bomonitor.properties.getProperty("log.history.dir")+"loghistory");
        if(currentHistory.length()>=Integer.parseInt(bomonitor.properties.getProperty("log.history.maxsize"))*1024){
            currentHistory=rotateFile();
        }
        return currentHistory;
    }
    private File rotateFile(){
        File path = new File(bomonitor.properties.getProperty("log.history.dir"));
        List<File> historyFiles=Arrays.asList(path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("loghistory");
            }
        }));
        if(historyFiles.isEmpty()){
            File fl = new File(bomonitor.properties.getProperty("log.history.dir")+"loghistory");
            try {
                fl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            historyFiles.add(fl);
        }
        Collections.sort(historyFiles);
        return historyFiles.get(0);

    }
}
