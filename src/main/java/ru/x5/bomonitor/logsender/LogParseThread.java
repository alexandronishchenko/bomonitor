package ru.x5.bomonitor.logsender;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.logsender.consumers.QueueSaver;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

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
                //TODO: Сделать: если файл поменялся. Берем сжатый предыдущий. Разархивируем его. Проверяем длинну файла с кэшем длинны. Если длинна больше - досохраняем строки и удаляем файл. Только затем обновляем кэш.
                checkAndLoadLastStrings();
                returnToCurrentLogFile();
                continue;
            }
            sleep();
            setCurrentFileInput(logFile);//filechannel and fileinputstream
            try {
                fileInputStream.getChannel().position(this.recordEntity.getFilePosition());
            } catch (IOException e) {
                e.printStackTrace();
                logger.insertRecord(this, "Channel was not available.", LogLevel.error);
            }
            readString();
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
        if (createdTime == crTime || crTime != mdTime) same = true;//!!!
        //if (this.recordEntity.getTime().getTime() < crTime) same = false;
        return same;
    }

    void readString() {
        String line = null;
        int i = 0;
        long pos;
        char c;
        try {
            StringBuilder sb = new StringBuilder();
            //читает строку
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
                    //если конец строки, то записываем размер файла, позицию курсора.
                    sendLine(sb.toString());
                    this.recordEntity.setFileSize(logFile.length());
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

    //Service methods
    public boolean isRunning() {
        return running;
    }

    File getPreviousFile() {
        File previous = null;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(bomonitor.properties.getProperty("log.arch.datepattern"));
        String dt = sdf.format(now);
        String fileName = bomonitor.properties.getProperty("log.dir") + "/bo_server.log-" + dt + ".gz";
        previous = new File(fileName);
        return previous;

    }

    void updateCreatedTime(){
        BasicFileAttributes fileAttributes = null;
        try {
            fileAttributes = Files.readAttributes(Paths.get(String.valueOf(logFile)), BasicFileAttributes.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
        createdTime=fileAttributes.creationTime().toMillis();
    }
    File unzipFile() {
        File INPUT_GZIP_FILE = getPreviousFile();
        File OUTPUT_FILE = new File(bomonitor.properties.getProperty("log.history.dir") + "OLD");
        byte[] buffer = new byte[1024];

        try {

            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(INPUT_GZIP_FILE));

            FileOutputStream out =
                    new FileOutputStream(OUTPUT_FILE);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return OUTPUT_FILE;
    }

    void checkAndLoadLastStrings() {
        File old = unzipFile();
        if (old.exists()) {
            //read strings.
            logFile=old;
            setCurrentFileInput(logFile);
            if(logFile.length()>recordEntity.getFileSize()) {
                try {
                    while (fileInputStream.available() > 0) {
                        readString();
                    }
                } catch (IOException e) {
                    logger.insertRecord(this, "Cant load old-file input. Or no strings", LogLevel.error);
                }
            }
            logFile.delete();
            logFile=new File(recordEntity.getFileName());
            setCurrentFileInput(logFile);
        }
    }

    public void setCurrentFileInput(File logFile) {
        try {
            fileInputStream = new FileInputStream(logFile);
            fc = fileInputStream.getChannel();
        } catch (FileNotFoundException e) {
            logger.insertRecord(this, "Log file does not exist." + this.logFile.getAbsolutePath(), LogLevel.warn);
            //e.printStackTrace();
        }    }

    void sleep(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            //e.printStackTrace();
            logger.insertRecord(this, "Thread is dead.", LogLevel.error);
        }
    }
    void returnToCurrentLogFile(){
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
        this.recordEntity.setFileSize(0L);
        cache.cacheRecord();
        setCurrentFileInput(new File(recordEntity.fileName));//
    }
}
