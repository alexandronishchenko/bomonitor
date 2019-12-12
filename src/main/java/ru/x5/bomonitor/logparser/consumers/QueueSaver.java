package ru.x5.bomonitor.logparser.consumers;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.ArrayList;
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
        long currentHistoryLenth=currentHistory.length();
        long maxLenth=Integer.parseInt(bomonitor.properties.getProperty("log.history.maxsize"))*1024*20;
        if(currentHistoryLenth>=maxLenth){
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
            File fl = new File(path+"loghistory");
            try {
                fl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            historyFiles.add(fl);
        }else{
            //int iter = historyFiles.size();
            ArrayList<String> names = new ArrayList<>();
            for(File fl : historyFiles){
                names.add(fl.getName().replaceAll("loghistory",""));
            }
            String name=null;
            if(names.size()>1){
                Collections.sort(names);
                int num=Integer.parseInt(names.get(names.size()-1))+1;
                name = path+"/loghistory"+num;
            }else {
                name=path+"/loghistory1";
            }

            File arch = new File(name);
            File old = new File(path+"/loghistory");
            old.renameTo(arch);

            File fl = new File(path+"/loghistory");
            try {
                fl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //historyFiles.add(fl);
            return fl;
        }
        Collections.sort(historyFiles);
        return historyFiles.get(0);

    }
}
