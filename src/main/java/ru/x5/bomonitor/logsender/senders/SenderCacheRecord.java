package ru.x5.bomonitor.logsender.senders;

import java.io.Serializable;

/**
 * Объект позиции записи в файле хистори.
 */
public class SenderCacheRecord implements Serializable {
    private long position;
    private String fileName;

    /**
     * Сеттеры и геттеры.
     *
     * @return
     */
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
