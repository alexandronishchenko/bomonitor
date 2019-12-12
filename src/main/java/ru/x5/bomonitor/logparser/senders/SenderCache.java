package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Основной класс отправки сообщений.
 * Имеет данные о всех файлах истории, кэша отправки, размера очереди в памяти, сами сообщения. Получает
 * и управляет файлами стории. Singletone. Имеет информацию об успешности отправки во все консьюмеры.
 */
public class SenderCache {

    Logger logger = bomonitor.getLogger();
    /**
     * Инстанс.
     */
    private static SenderCache instance;
    /**
     * Локер для методов.
     */
    Object lock = new Object();
    /**
     * Файл кэша отправки записей.
     */
    private File cache;
    /**
     * Сообщения, загруженные в память.
     */
    Deque<String> messages;
    /**
     * Максимальный размер очереди.
     */
    private int capacity;
    /**
     * Текущая запись из файла истории (позиция и файл(опционально))
     */
    private SenderCacheRecord cacheRecord;
    /**
     * Лист коннекторов.
     */
    List<SenderConnector> connectors = new ArrayList<>();
    /**
     * Текущий файл истории.
     */
    private File history;


    public static SenderCache getInstance() {
        if (instance == null) {
            instance = new SenderCache();
        }
        return instance;
    }

    /**
     * Инициализирует класс.
     * Необходимые параметры:
     * log.sender.cache файл кэширования записи
     * sender.cache.capacity максимальный размер очереди
     * log.consumers консьюмеры для отправки. (Есть только kafka,out).
     */
    private SenderCache() {
        this.cache = new File(bomonitor.properties.getProperty("log.sender.cache"));
        if (!cache.exists()) {
            try {
                cache.createNewFile();
                logger.insertRecord(this, "Created new cache for sender.", LogLevel.debug);
            } catch (IOException e) {
                e.printStackTrace();
                logger.insertRecord(this, "Error at creating cache sender file.", LogLevel.error);
            }
        }
        if (cache.length() == 0) {
            this.cacheRecord = new SenderCacheRecord();
            this.cacheRecord.setPosition(0);
            //this.cacheRecord.setFileName();
            cacheRecord();
            logger.insertRecord(this, "No cached record creating new.", LogLevel.debug);
        } else {
            cacheRecord = getRecord();
            logger.insertRecord(this, "Loaded cache for sender", LogLevel.debug);
        }
        this.messages = new ArrayDeque<>();
        this.capacity = Integer.parseInt(bomonitor.properties.getProperty("sender.cache.capacity"));
        getHistoryFile();
        List<String> conNames = Arrays.asList(bomonitor.properties.getProperty("log.consumers").split(","));
        for (String con : conNames) {
            switch (con) {
                case "kafka":
                    connectors.add(new KafkaSenderConnector());
                    logger.insertRecord(this, "Created kafka consumer.", LogLevel.info);
                    break;
                case "out":
                    connectors.add(new OutSenderConnector());
                    logger.insertRecord(this, "Created out consumer.", LogLevel.info);
                    break;
            }
        }
    }

    /**
     * Очистка позиции кэша записи (устанавливает позицию в 0)
     */
    public void dropCache() {
        logger.insertRecord(this, "Drop cache for sender", LogLevel.debug);
        this.cacheRecord.setPosition(0);
    }


    /**
     * Загружает сообщения из файлов. Либо пока файл не кончился, либо до максимального размера.
     *
     * @return Закончился ли файл.
     */
    boolean updateMessages() {
        synchronized (lock) {
            boolean fileFinished = false;
            FileInputStream inputLog = null;
            getHistoryFile();
            try {
                if (history != null) inputLog = new FileInputStream(history);
            } catch (FileNotFoundException | NullPointerException e) {
                logger.insertRecord(this, "No history file still.", LogLevel.debug);
                //e.printStackTrace();
            }
            if (inputLog != null) {
                logger.insertRecord(this, "Started loading strings.", LogLevel.debug);
                this.cacheRecord.setFileName(history.getName());
                while (messages.size() <= capacity && !fileFinished) {
                    fileFinished = loadString(inputLog);
                }
            }

            return fileFinished;
        }

    }

    /**
     * Предварительно обновляет записи в очереди. Если записей нет - вызывает попытку удаление файла.
     *
     * @return Первая запись в очереди.
     */
    synchronized String getFirst() {
        if (messages.size() == 0) {
            logger.insertRecord(this, "message size == 0", LogLevel.debug);
            removeOldFile();
            updateMessages();
        }
        if (messages.size() != 0) {
            return messages.getFirst();
        } else {
            return null;
        }
    }

    synchronized void uncacheFirst() {
        messages.removeFirst();
    }

    /**
     * Записывает кэш записи в файл-кэша (позиция в файле истории)
     */
    //IO for notes
    synchronized void cacheRecord() {
        ObjectOutputStream os = getOutputStream();
        try {
            os.writeObject(cacheRecord);
            os.flush();
            os.close();
        } catch (IOException e) {
            logger.insertRecord(this, "Couldnot cache the record for sender. IO", LogLevel.error);
            //e.printStackTrace();
        }
    }

    /**
     * Загружает данные о кэшированной записи (кэшированная позиция в файле истории)
     *
     * @return
     */
    synchronized SenderCacheRecord getRecord() {
        SenderCacheRecord rec = null;
        ObjectInputStream is = getInputStream();
        try {
            while (is.available() >= 0) {
                rec = ((SenderCacheRecord) is.readObject());
            }
            is.close();
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            logger.insertRecord(this, "Couldnot load cache record for sender. IOException | ClassNotFoundException | NullPointerException", LogLevel.error);
            e.printStackTrace();
        }
        return rec;
    }

    /**
     * Получение потока чтения из файла кэша записи.
     *
     * @return
     */
    //Stream getters
    synchronized private ObjectInputStream getInputStream() {
        ObjectInputStream is = null;
        if (cache.length() > 0) {
            try {
                is = new ObjectInputStream(new FileInputStream(cache));
            } catch (IOException ex) {
                logger.insertRecord(this, "InputStream IO", LogLevel.error);
                //ex.printStackTrace();
            }
        }
        return is;

    }

    /**
     * Поток записи в файл кэша записи.
     *
     * @return
     */
    synchronized private ObjectOutputStream getOutputStream() {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new FileOutputStream(cache));
        } catch (IOException ex) {
            logger.insertRecord(this, "OutputStream IO", LogLevel.error);
            //ex.printStackTrace();
        }
        return os;
    }


    /**
     * Загрузка строки из файлв истории.
     *
     * @param inputLog поток ввода истории.
     * @return закончился ли файл.
     */
    //job with file
    boolean loadString(FileInputStream inputLog) {
        boolean fileFinished = false;
        FileChannel fc = inputLog.getChannel();
        int i = 0;
        long pos;
        char c;
        try {
            inputLog.getChannel().position(this.cacheRecord.getPosition());
        } catch (IOException e) {
            logger.insertRecord(this, "Channel was not sc handled.", LogLevel.error);
            //e.printStackTrace();
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
                    logger.insertRecord(this, "String loaded: " + sb.toString(), LogLevel.debug);
                    sb = new StringBuilder();
                } else {
                    sb.append(c);
                }
            }
            // messages.addLast(sb.toString());
            fileFinished = true;
        } catch (IOException e) {
            // e.printStackTrace();
            logger.insertRecord(this, "Error at reading symbols at while.", LogLevel.error);
        }
        return fileFinished;
    }


    /**
     * Получение текущего фала истории. Получает либо без номера, если файл один, в противном случае выбирает меньший порядковый номер
     * из списка файлов. В то время, как поток записи пишет в наибольший.
     */
    private void getHistoryFile() {
        File path = new File(bomonitor.properties.getProperty("log.history.dir"));
        List<File> historyFiles = Arrays.asList(path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("loghistory");
            }
        }));
        if (historyFiles.isEmpty()) {
            history = null;
            logger.insertRecord(this, "No history", LogLevel.debug);
        } else {
            Collections.sort(historyFiles);
            if (historyFiles.size() > 1) {
                ArrayList<Integer> names = new ArrayList<>();
                for (File fl : historyFiles) {
                    try {
                        names.add(Integer.parseInt(fl.getName().replaceAll("loghistory", "")));
                    } catch (NumberFormatException e) {
                        names.add(0);
                    }
                }
                //ames.stream().forEach(i -> Integer.parseInt(i));
                Collections.sort(names);
                //history = historyFiles.get(1);
                for (File fl : historyFiles) {
                    //String sn = fl.getName();
                    if (fl.getName().equals("loghistory" + names.get(1))) history = fl;
                }
            } else {
                history = historyFiles.get(historyFiles.size() - 1);
                logger.insertRecord(this, "History will: " + history.getName(), LogLevel.debug);
            }

        }
    }

    /**
     * Удаляет отправленный файл, если он не дефолтный.
     */
    private void removeOldFile() {
        try {
            if (history.getName().equals("loghistory")) {
                getHistoryFile();
                return;
            }
            history.delete();
            dropCache();
            getHistoryFile();
        } catch (NullPointerException e) {
            logger.insertRecord(this, "Nothing to delete.", LogLevel.debug);
            //e.printStackTrace();
        }

    }


    /**
     * Метод доступа к отправке только в пакете. Переотправляет строку, пока не получится во все консьюмеры. И записывает, что строка отправлена.
     * Чтение файла начнется в случае чего, начиная от этого кэша. ТО есть неотправленная успешно. Будет переотправлена и далее по порядку.
     */
    void sendFirst() {
        boolean success = false;
        try {
            while (!success) {
                for (SenderConnector connector : connectors) {
                    if (!connector.sendLine(getFirst())) {
                        success = false;
                        System.out.println("retdsafsdfry!" + getFirst());
                        break;
                    } else {
                        success = true;
                    }
                }
            }
            cacheRecord();
            messages.removeFirst();
        } catch (NullPointerException e) {
            logger.insertRecord(this, "No messages.", LogLevel.debug);
        }
    }
}
