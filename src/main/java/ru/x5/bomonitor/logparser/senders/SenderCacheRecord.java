package ru.x5.bomonitor.logparser.senders;

import java.io.Serializable;

public class SenderCacheRecord implements Serializable {
    private long position;
    private String fileName;

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
