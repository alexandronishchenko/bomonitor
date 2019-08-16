package ru.x5.bomonitor.Logger;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final File log = new File(bomonitor.properties.getProperty("log"));
    public Logger(){
        System.out.println("Log file is "+bomonitor.properties.getProperty("log"));
    }


   public void insertRecord(String record,LogLevel level){
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
//        if(!log.exists()){
//
//        }
        //log.

    }
}
