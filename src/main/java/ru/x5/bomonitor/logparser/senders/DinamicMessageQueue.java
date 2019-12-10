package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.*;

public class DinamicMessageQueue {
    private Deque<String> messages;
    private File history;
    private SenderCache senderCache;
    List<SenderConnector> connectors=new ArrayList<>();

    public DinamicMessageQueue() {
        messages=new ArrayDeque<>();
        List<String> conNames=Arrays.asList(bomonitor.properties.getProperty("log.consumers").split(","));
        for(String con : conNames){
            switch (con){
                case "kafka":connectors.add(new KafkaSenderConnector());
                break;
                case "out":connectors.add(new OutSenderConnector());
                break;
            }
        }
        senderCache=SenderCache.getInstance(messages);
        if(history==null){
            getHistoryFile();
        }
    }


    public void sendFirst(){
        //Если файла нет ждем 10 секунд
        getHistoryFile();
        if(history==null){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(messages.size()==0){
            try {
                Thread.sleep(10000);
                senderCache.updateMessages(history);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            String first = senderCache.getFirst(history);
            for(SenderConnector connector : connectors){
                if(!connector.sendLine(first)){break;}else {
                    senderCache.cacheRecord();
                    senderCache.uncacheFirst();
                }
            }
        }


    }


    private void getHistoryFile() {
        File path = new File(bomonitor.properties.getProperty("log.history.dir"));
        List<File> historyFiles = Arrays.asList(path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("loghistory");
            }
        }));
        if (historyFiles.isEmpty()) {
            history = null;
        } else {
            Collections.sort(historyFiles);
            history = historyFiles.get(historyFiles.size() - 1);
        }
    }
}
