package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.logparser.CachedRecordEntity;

import java.io.*;
import java.util.Date;
import java.util.Deque;

public class SenderCache {
    private File cache;
    Deque<String> messages;
    private int capacity;
    private SenderCacheRecord cacheRecord;

    public SenderCache(Deque<String> messages) {
        this.cache=new File(bomonitor.properties.getProperty("log.sender.cache"));
        if (!cache.exists()) {
            try {
                cache.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (cache.length() == 0) {
            this.cacheRecord=new SenderCacheRecord();
            this.cacheRecord.setPosition(0);
            cacheRecord();
        }else{
            cacheRecord=getRecord();
        }
        this.messages=messages;
        this.capacity= Integer.parseInt(bomonitor.properties.getProperty("sender.cache.capacity"));
    }

    void updateMessages(File history){

    }
    String getFirst(){
        return null;
    }
    void uncacheFirst(){

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
                rec=((SenderCacheRecord) is.readObject());
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
}
