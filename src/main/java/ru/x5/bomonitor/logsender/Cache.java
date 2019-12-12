package ru.x5.bomonitor.logsender;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Работа с лог файлами. Singletone.
 * Параметры:
 * cache.file файл кэша пизиций.
 * log.files файлы для отправки.
 */
public class Cache {

    Logger logger = bomonitor.getLogger();
    /**
     * Инстанс.
     */
    private static Cache instance;
    /**
     * Файл кэша позиции чтения.
     */
    private File cache;
    /**
     * Объекты кэша позиций чтения из файлов логов.
     */
    private HashSet<CachedRecordEntity> cashedRecords;


    /**
     * Конструктор. Проверяет и создает кэш-файл, проверяет наличие лога.
     */
    private Cache() {
        this.cache = new File(bomonitor.properties.getProperty("cache.file"));
        if (!cache.exists()) {
            logger.insertRecord(this, "Creating new cache for read.", LogLevel.info);
            try {
                cache.createNewFile();
            } catch (IOException e) {
                //e.printStackTrace();
                logger.insertRecord(this, "Couldnt create cache..", LogLevel.error);
            }
        }
        List<String> logFiles = Arrays.asList(bomonitor.properties.getProperty("log.files").split(","));
        //Initialize records from file.
        cashedRecords = new HashSet<>();
        if (cache.length() == 0) {
            logger.insertRecord(this, "No records. Creating new.", LogLevel.debug);
            for (String file : logFiles) {
                File log = new File(file);
                cashedRecords.add(new CachedRecordEntity("", new Date(), log.getName(), log.length(), 0));
                logger.insertRecord(this, "Created cache for " + file, LogLevel.info);
            }
            cacheRecord();
            logger.insertRecord(this, "Cache synced.", LogLevel.info);
        } else {
            ObjectInputStream is = getInputStream();
            try {
                logger.insertRecord(this, "Loading cache.", LogLevel.info);
                while (is.available() >= 0) {
                    cashedRecords.add((CachedRecordEntity) is.readObject());
                }
                is.close();
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                logger.insertRecord(this, "Loading has an error: IOException | ClassNotFoundException | NullPointerException.", LogLevel.error);
                //e.printStackTrace();
            }
        }


    }

    /**
     * Получение инстанса.
     *
     * @return
     */
    static Cache getInstance() {
        if (null == instance) {
            instance = new Cache();
        }
        return instance;
    }


    /**
     * Записать кэш из ОЗУ.
     */
    synchronized void cacheRecord() {
        ObjectOutputStream os = getOutputStream();
        try {
            for (CachedRecordEntity rec : cashedRecords) {
                os.writeObject(rec);
            }
            os.flush();
            os.close();
        } catch (IOException e) {
            logger.insertRecord(this, "Cache write error: IOException.", LogLevel.error);
            e.printStackTrace();
        }
    }


    /**
     * Получение записи кэша по файлу.
     *
     * @param file файл лога.
     * @return
     */
    synchronized CachedRecordEntity getRecordForFile(File file) {
        CachedRecordEntity record = null;
        for (CachedRecordEntity rec : cashedRecords) {
            if (rec.fileName.equals(file.getName())) record = rec;
        }
        if (null == record) {
            record = new CachedRecordEntity("", new Date(), file.getName(), file.length(), 0);
        }
        cashedRecords.add(record);
        return record;
    }

    //Stream getters
    synchronized private ObjectInputStream getInputStream() {
        ObjectInputStream is = null;
        if (cache.length() > 0) {
            try {
                is = new ObjectInputStream(new FileInputStream(cache));
            } catch (IOException ex) {
                logger.insertRecord(this, "InputStream error.", LogLevel.error);
                //ex.printStackTrace();
            }
        }
        return is;

    }

    synchronized private ObjectOutputStream getOutputStream() {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new FileOutputStream(cache));
        } catch (IOException ex) {
            logger.insertRecord(this, "OutputStream error.", LogLevel.error);
            //ex.printStackTrace();
        }
        return os;
    }
}
