package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

public class SenderCache {

    private static SenderCache instance;

    Object lock = new Object();
    private File cache;
    Deque<String> messages;
    private int capacity;
    private SenderCacheRecord cacheRecord;
    List<SenderConnector> connectors=new ArrayList<>();
    private File history;

    public static SenderCache getInstance() {
        if (instance == null) {
            instance = new SenderCache();
        }
        return instance;
    }

    //constructor
    private SenderCache() {
        this.cache = new File(bomonitor.properties.getProperty("log.sender.cache"));
        if (!cache.exists()) {
            try {
                cache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cache.length() == 0) {
            this.cacheRecord = new SenderCacheRecord();
            this.cacheRecord.setPosition(0);
            //this.cacheRecord.setFileName();
            cacheRecord();
        } else {
            cacheRecord = getRecord();
        }
        this.messages = new ArrayDeque<>();
        this.capacity = Integer.parseInt(bomonitor.properties.getProperty("sender.cache.capacity"));
        getHistoryFile();
        List<String> conNames=Arrays.asList(bomonitor.properties.getProperty("log.consumers").split(","));
        for(String con : conNames){
            switch (con){
                case "kafka":connectors.add(new KafkaSenderConnector());
                    break;
                case "out":connectors.add(new OutSenderConnector());
                    break;
            }
        }
    }

    public void dropCache(){
        this.cacheRecord.setPosition(0);
    }


    boolean updateMessages() {
        synchronized (lock) {
            boolean fileFinished=false;
            FileInputStream inputLog = null;
            getHistoryFile();
            try {
                if(history!=null) inputLog = new FileInputStream(history);
            } catch (FileNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }
            if (inputLog != null) {
                this.cacheRecord.setFileName(history.getName());
                while (messages.size() <= capacity && !fileFinished) {
                    fileFinished=loadString(inputLog);
                }
            }

            return fileFinished;
        }

    }

    synchronized String getFirst() {
        if(messages.size()==0) {
            removeOldFile();

            updateMessages();
        }
        if(messages.size()!=0){
            return messages.getFirst();
        }else {return null;}
    }

    synchronized void uncacheFirst() {
        messages.removeFirst();
    }


    //IO for notes
    synchronized void cacheRecord() {
        ObjectOutputStream os = getOutputStream();
        try {
            os.writeObject(cacheRecord);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized SenderCacheRecord getRecord() {
        SenderCacheRecord rec = null;
        ObjectInputStream is = getInputStream();
        try {
            while (is.available() >= 0) {
                rec = ((SenderCacheRecord) is.readObject());
            }
            is.close();
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        return rec;
    }

    //Stream getters
    synchronized private ObjectInputStream getInputStream() {
        ObjectInputStream is = null;
        if (cache.length() > 0) {
            try {
                is = new ObjectInputStream(new FileInputStream(cache));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return is;

    }

    synchronized private ObjectOutputStream getOutputStream() {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new FileOutputStream(cache));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return os;
    }


    //job with file
    boolean loadString(FileInputStream inputLog) {
        boolean fileFinished = false;
//        try {
//            if (inputLog.available() < 0) {cacheRecord.setPosition(0);return true;}
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        FileChannel fc = inputLog.getChannel();
        int i = 0;
        long pos;
        char c;
        try {
            inputLog.getChannel().position(this.cacheRecord.getPosition());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            while ((i = inputLog.read()) != -1) {
                // get channel position
                this.cacheRecord.setPosition(fc.position());

                // integer to character
                c = (char) i;
                if (c == '\r' || c == '\n') {
                messages.addFirst(sb.toString());
                sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }
           // messages.addLast(sb.toString());
            fileFinished=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileFinished;
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
            if(historyFiles.size()>1){
                ArrayList<Integer> names = new ArrayList<>();
                for(File fl : historyFiles){
                    try {
                        names.add(Integer.parseInt(fl.getName().replaceAll("loghistory", "")));
                    }catch (NumberFormatException e){
                        names.add(0);
                    }
                }
                //ames.stream().forEach(i -> Integer.parseInt(i));
                Collections.sort(names);
                //history = historyFiles.get(1);
                for(File fl : historyFiles){
                    //String sn = fl.getName();
                    if(fl.getName().equals("loghistory"+names.get(1))) history=fl;
                }
            }else{
                history = historyFiles.get(historyFiles.size() - 1);
            }

        }
    }
    private void removeOldFile(){
        try{
            if(history.getName().equals("loghistory")){
                getHistoryFile();
                return;
            }
            history.delete();
            dropCache();
            getHistoryFile();
        }catch (NullPointerException e){
            System.out.println("No history file");
            //e.printStackTrace();
        }

    }


    void sendFirst(){
        boolean success=false;
        try {
            while (!success) {
                for (SenderConnector connector : connectors) {
                    if (!connector.sendLine(getFirst())) {
                        success = false;
                        System.out.println("retdsafsdfry!" + getFirst());
                        break;
                    } else {
                        success = true;
                    }
                }

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

            }
            cacheRecord();
            messages.removeFirst();
        }catch (NullPointerException e){
            //e.printStackTrace();
        }
    }
}
