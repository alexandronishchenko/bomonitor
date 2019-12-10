package ru.x5.bomonitor.logparser.senders;

import java.io.Serializable;

public class SenderCacheRecord implements Serializable {
    private long position;

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

}
