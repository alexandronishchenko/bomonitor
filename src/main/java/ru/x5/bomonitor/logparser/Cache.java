package ru.x5.bomonitor.logparser;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Cache {
    private static Cache instance;
    private File cache;
    private HashSet<CachedRecordEntity> cashedRecords;

    private Cache() {
        this.cache = new File(bomonitor.properties.getProperty("cache.file"));
        cashedRecords = new HashSet<>();
    }

    static Cache getInstance() {
        if (null == instance) {
            instance = new Cache();
        }
        return instance;
    }


    synchronized void cacheRecord() {

    }

    synchronized CachedRecordEntity getRecord() {

        return null;
    }

    synchronized CachedRecordEntity getRecordForFile(File file) {
        syncList();
        CachedRecordEntity record = null;
        for (CachedRecordEntity rec : cashedRecords) {
            if (rec.fileName.equals(file.getName())) record = rec;
        }
        if (null == record) {
            record = new CachedRecordEntity("", new Date(), file.getName(), file.length(), 0);
        }
        cashedRecords.add(record);
        updateList();
        return record;
    }

    synchronized void updateRecord(CachedRecordEntity recordEntity){
        cashedRecords.add(recordEntity);
        updateList();
    }
    synchronized private void updateList() {
        ObjectOutputStream os = getOutputStream();
        try {
            for (CachedRecordEntity rec : cashedRecords) {
                os.writeObject(rec);
            }
            os.flush();
            os.close();
        }catch (IOException e){
            e.printStackTrace();
        }
//            while (is.available()>0) {
//                cashedRecords.add((CachedRecordEntity) is.readObject());
//            }
    }

    synchronized private void syncList() {
        ObjectInputStream is = getInputStream();
        try {
            while (is.available() > 0) {
                cashedRecords.add((CachedRecordEntity) is.readObject());
            }
            is.close();
        }catch (IOException | ClassNotFoundException | NullPointerException e){
            e.printStackTrace();
        }
    }

    synchronized private ObjectInputStream getInputStream() {
        ObjectInputStream is=null;
        if(cache.length()>0) {
            try {
                is = new ObjectInputStream(new FileInputStream(cache));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return is;

    }

    synchronized private ObjectOutputStream getOutputStream() {
        ObjectOutputStream os=null;
        try {
            os = new ObjectOutputStream(new FileOutputStream(cache));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return os;
    }
}
