package ru.x5.bomonitor.Logger;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static File log= new File(bomonitor.properties.getProperty("log"));
    public Logger(){
        System.out.println("Log file is "+bomonitor.properties.getProperty("log"));
    }


   public void insertRecord(String record,LogLevel level){
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
        System.out.println(curDate+" "+record);
        if(writer!=null){
            try {
                if(level.equals(LogLevel.error)){
                writer.write(curDate+" "+record+"\r\n");
                writer.flush();
                writer.close();
                }
            } catch (IOException e) {
                System.out.println("Cannot write to file.");
            }
        }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!today){
            System.out.println("not today");
            //System.out.println();
            log.renameTo(new File(log.getAbsolutePath()+date.replaceAll("-","")));
            log= new File(bomonitor.properties.getProperty("log"));
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
