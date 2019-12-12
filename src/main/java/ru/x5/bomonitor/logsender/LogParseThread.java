package ru.x5.bomonitor.logsender;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.logsender.consumers.QueueSaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

/**
 * Поток чтения из определенного файла. Верхнеуровневый для доступа сервиса. Именно они перезапускаются для чтения.
 */
public class LogParseThread implements Runnable, LogMonitor {
    /**
     * Записывает данные в хистори-файлы.
     */
    private QueueSaver queueSaver;
    /**
     * Работает ли поток.
     */
    private volatile boolean running;
    /**
     * Класс управления кэшами.
     */
    private Cache cache;
    /**
     * Запись кэша.
     */
    private CachedRecordEntity recordEntity;
    /**
     * Файл лога.
     */
    private File logFile;
    /**
     * Время создания лога.
     */
    private Long createdTime;
    /**
     * Поток чтения из лога.
     */
    private FileInputStream fileInputStream;
    private Logger logger = bomonitor.getLogger();
    /**
     * Канал ввода. Для получения позиции.
     */
    private FileChannel fc = null;

    /**
     * Конструктор принимает файл для мониторинга.
     *
     * @param logFile файл лога.
     */
    public LogParseThread(String logFile) {
        this.logFile = new File(logFile);
        BasicFileAttributes fileAttributes = null;
        try {
            fileAttributes = Files.readAttributes(Paths.get(logFile), BasicFileAttributes.class);
        } catch (IOException e) {
            logger.insertRecord(this, "Read attr for file failed. IO", LogLevel.error);
        }
        this.queueSaver = new QueueSaver();
        try {
            createdTime = fileAttributes.creationTime().toMillis();
        } catch (NullPointerException e) {
            logger.insertRecord(this, "No file. " + logFile, LogLevel.error);
        }
        this.running = true;
        logger.insertRecord(this, "Parsing of log: " + this.logFile.getAbsolutePath() + " started.", LogLevel.info);
        this.cache = Cache.getInstance();
        recordEntity = cache.getRecordForFile(this.logFile);
    }

    /**
     * Непосредственный поток.
     */
    @Override
    public void run() {
        while (running) {
            if (!isSameFile()) {
                long curTime = 0;
                try {
                    curTime = Files.readAttributes(Paths.get(this.logFile.getAbsolutePath()), BasicFileAttributes.class).creationTime().toMillis();
                } catch (IOException e) {
                    //e.printStackTrace();
                    logger.insertRecord(this, "Read attr failed at run. IO", LogLevel.warn);
                }
                createdTime = curTime;
                this.recordEntity.setFilePosition(0);
                this.recordEntity.setTime(new Date(curTime));
                cache.cacheRecord();
                continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                logger.insertRecord(this, "Thread is dead.", LogLevel.error);
            }
            try {
                fileInputStream = new FileInputStream(logFile);
                fc = fileInputStream.getChannel();
            } catch (FileNotFoundException e) {
                logger.insertRecord(this, "Log file does not exist." + this.logFile.getAbsolutePath(), LogLevel.warn);
                //e.printStackTrace();
            }
            int i = 0;
            long pos;
            char c;
            try {
                fileInputStream.getChannel().position(this.recordEntity.getFilePosition());
            } catch (IOException e) {
                e.printStackTrace();
                logger.insertRecord(this, "Channel was not available.", LogLevel.error);
            }
            try {
                StringBuilder sb = new StringBuilder();
                while ((i = fileInputStream.read()) != -1) {
                    //Если файл поменялся - прерываем цикл и открываем все заново.
                    if (!isSameFile()) {
                        logger.insertRecord(this, "Log file was changed.", LogLevel.info);
                        break;
                    }

                    // get channel position
                    this.recordEntity.setFilePosition(fc.position());

                    // integer to character
                    c = (char) i;
                    if (c == '\r' || c == '\n') {
                        sendLine(sb.toString());
                        cache.cacheRecord();
                        logger.insertRecord(this, "Saved record: " + sb.toString(), LogLevel.debug);
                        sb = new StringBuilder();
                    } else {
                        sb.append(c);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileInputStream.close();
        } catch (IOException e) {
            //e.printStackTrace();
            logger.insertRecord(this, "Couldnt close FIS. IO", LogLevel.error);
        }
    }


    /**
     * Отправить строку на обработку для сохранения в хистори.
     *
     * @param line
     */
    private void sendLine(String line) {
        this.queueSaver.putLine(line);
    }

    /**
     * Проверка - поменялся ли файл. ext4 не записывает данные о создании файла при файле менее 256 байт. Поэтому проверяется совпадение модификации и создания.
     *
     * @return
     */
    private boolean isSameFile() {
        //TODO: Сделать: если файл поменялся. Берем сжатый предыдущий. Разархивируем его. Проверяем длинну файла с кэшем длинны. Если длинна больше - досохраняем строки и удаляем файл. Только затем обновляем кэш.
        boolean same = false;
        long crTime = 0;
        long mdTime = 0;
        try {
            crTime = Files.readAttributes(Paths.get(this.logFile.getAbsolutePath()), BasicFileAttributes.class).creationTime().toMillis();
            mdTime = Files.readAttributes(Paths.get(this.logFile.getAbsolutePath()), BasicFileAttributes.class).lastModifiedTime().toMillis();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cant load time attributes.");
        }
        if (createdTime == crTime || crTime == mdTime) same = true;
        //if (this.recordEntity.getTime().getTime() < crTime) same = false;
        return same;
    }

    //Service methods
    public boolean isRunning() {
        return running;
    }
}
