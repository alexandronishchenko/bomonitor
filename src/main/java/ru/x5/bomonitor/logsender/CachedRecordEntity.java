package ru.x5.bomonitor.logsender;

import java.io.Serializable;
import java.util.Date;

/**
 * Объект кэшированной записи позиции чтения из лога.
 * Большинство полей неиспользуются вовсе.
 */
public class CachedRecordEntity implements Serializable {
    String value;
    Date time;
    String fileName;
    long fileSize;
    long filePosition;

    public CachedRecordEntity(String value, Date time, String fileName, long fileSize, long filePosition) {
//        this.value = value;
        this.time = time;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePosition = filePosition;
    }

//    public String getValue() {
//        return value;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getFilePosition() {
        return filePosition;
    }

    public void setFilePosition(long filePosition) {
        this.filePosition = filePosition;
    }

    @Override
    public int hashCode() {
        return this.fileName.length();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CachedRecordEntity) {
            return this.fileName.equals(((CachedRecordEntity) obj).fileName);
        } else {
            return false;
        }

    }
}
