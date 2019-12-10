package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Deque;

public class SenderCache {

    private static SenderCache instance;

    Object lock=new Object();
    private File cache;
    Deque<String> messages;
    private int capacity;
    private SenderCacheRecord cacheRecord;

    private SenderCache(Deque<String> messages) {
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
            cacheRecord();
        } else {
            cacheRecord = getRecord();
        }
        this.messages = messages;
        this.capacity = Integer.parseInt(bomonitor.properties.getProperty("sender.cache.capacity"));
    }

    public static SenderCache getInstance(Deque<String> messages) {
        if (instance == null) {
            instance = new SenderCache(messages);
        }
        return instance;
    }

    void updateMessages(File history) {
        //TODO: check end of file. Open next.
synchronized (lock) {
    FileInputStream inputLog = null;
    try {
        inputLog = new FileInputStream(history);
    } catch (FileNotFoundException | NullPointerException e) {
        e.printStackTrace();
    }
    if (inputLog != null) {
        while (messages.size() <= capacity) {
            loadString(inputLog);
        }

    }
}
    }

    synchronized String getFirst(File history) {
        updateMessages(history);

        return messages.getFirst();
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
    void loadString(FileInputStream inputLog) {
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
                    messages.addLast(sb.toString());
//                    cacheRecord();
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
