package ru.x5.bomonitor.Logger;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static File log= new File(bomonitor.properties.getProperty("log"));
    public Logger(){
        System.out.println("Log file is "+bomonitor.properties.getProperty("log"));
    }


   public void insertRecord(Object o,String record,LogLevel level){
        logRotate();
        BufferedWriter writer=null;
        try {
            if(!log.exists())log.createNewFile();
            writer=new BufferedWriter(new FileWriter(log,true));
        } catch (IOException e) {
            System.out.println("Cannot access to file.");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("YY-MM-dd HH:mm:ss");
        String curDate = sdf.format(new Date());
        String levelS = level.name().toUpperCase();
        record="["+levelS+"] "+record;
        System.out.println(curDate+" <"+o.getClass().getName()+"> "+record);
        if(writer!=null){
            try {
                String propLevel=level.name();
                switch (bomonitor.properties.getProperty("log_level")){
                    case "debug":
                        if(propLevel.equals("debug")||propLevel.equals("info")||propLevel.equals("warn")||propLevel.equals("error")){write(writer,curDate+" <"+o.getClass().getName()+"> "+record+"\r\n");}
                        break;
                    case "info":
                        if(propLevel.equals("info")|| propLevel.equals("warn")||propLevel.equals("error")){write(writer,curDate+" <"+o.getClass().getName()+"> "+record+"\r\n");}
                        break;
                    case "warn":
                        if(propLevel.equals("warn")||propLevel.equals("error"))write(writer,curDate+" <"+o.getClass().getName()+"> "+record+"\r\n");
                        break;
                    case "error":
                        if(propLevel.equals("error"))write(writer,curDate+" <"+o.getClass().getName()+"> "+record+"\r\n");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Cannot write to file.");
            }
        }

    }
    private void write(BufferedWriter wr,String s) throws IOException {
        wr.write(s);
        wr.flush();
        wr.close();
    }
    void logRotate(){
        String file_name = log.getAbsolutePath();
        BasicFileAttributes attr = null;
        boolean today=false;
        String date="";
        try {
            attr = Files.readAttributes(Paths.get(file_name), BasicFileAttributes.class);
            String created_date= String.valueOf(attr.creationTime());
            long dt = new Date().getTime();
            SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
            date = smp.format(new Date(dt));
             today=date.equals(created_date.substring(0,10));
            if(!today) {
                System.out.println("not today");
                //System.out.println();
                log.renameTo(new File(log.getAbsolutePath() + date.replaceAll("-", "")));
                log = new File(bomonitor.properties.getProperty("log"));
                try {
                    log.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchFileException g) {
            //g.printStackTrace();
            System.out.println("No previous log file.");
        } catch (IOException e){
            System.out.println("Error with file");
        }


    }
}
