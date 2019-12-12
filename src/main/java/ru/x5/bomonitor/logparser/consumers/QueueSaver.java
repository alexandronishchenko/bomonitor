package ru.x5.bomonitor.logparser.consumers;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Класс для сохранения всех строк в файл истории. В полях только логгер.
 */
public class QueueSaver {
    Logger logger = bomonitor.getLogger();

    /**
     * Публичный метод для сохранения строки в историю.
     *
     * @param line Переданная для обработки строка.
     */
    public void putLine(String line) {
        saveLine(line);
    }

    /**
     * Сохраняет строку в файл, полученый из метода getHistoryFile()
     *
     * @param line Строка для сохранения.
     */
    private void saveLine(String line) {
        logger.insertRecord(this, "Try to save string: " + line, LogLevel.debug);
        File logFile = getHistoryFile();
        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(line + "\r");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод получения йфайла. Проверяет на размер и вызывает ротацию.
     *
     * @return Файл истории.
     */
    private File getHistoryFile() {
        File currentHistory = new File(bomonitor.properties.getProperty("log.history.dir") + "loghistory");
        long currentHistoryLenth = currentHistory.length();
        long maxLenth = Integer.parseInt(bomonitor.properties.getProperty("log.history.maxsize")) * 1024 * 1024;
        if (currentHistoryLenth >= maxLenth) {
            currentHistory = rotateFile();
        }
        return currentHistory;
    }

    /**
     * Проверяет и создает файлы ротированные. Возвращает наимболее молодой файл. Если такого нет, то создает новый и возвращает его.
     *
     * @return Ротированный файл.
     */
    private File rotateFile() {
        File path = new File(bomonitor.properties.getProperty("log.history.dir"));
        List<File> historyFiles = Arrays.asList(path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("loghistory");
            }
        }));
        if (historyFiles.isEmpty()) {
            logger.insertRecord(this, "No history files. Creating", LogLevel.debug);
            File fl = new File(path + "loghistory");
            try {
                fl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logger.insertRecord(this, "Error with creating loghistory file.", LogLevel.error);
            }
            historyFiles.add(fl);
        } else {
            logger.insertRecord(this, "Collecting history files.", LogLevel.debug);
            ArrayList<String> names = new ArrayList<>();
            for (File fl : historyFiles) {
                names.add(fl.getName().replaceAll("loghistory", ""));
            }
            String name = null;
            if (names.size() > 1) {
                Collections.sort(names);
                int num = Integer.parseInt(names.get(names.size() - 1)) + 1;
                name = path + "/loghistory" + num;
            } else {
                name = path + "/loghistory1";
            }
            logger.insertRecord(this, "Current history file will: " + name, LogLevel.debug);
            File arch = new File(name);
            File old = new File(path + "/loghistory");
            old.renameTo(arch);

            File fl = new File(path + "/loghistory");
            try {
                fl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //historyFiles.add(fl);
            return fl;
        }
        Collections.sort(historyFiles);
        return historyFiles.get(0);

    }
}
