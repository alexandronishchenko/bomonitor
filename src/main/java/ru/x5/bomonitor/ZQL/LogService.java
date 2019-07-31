package ru.x5.bomonitor.ZQL;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.ArrayList;

public class LogService extends Service {
    File log=null;
    ArrayList<ERROR> errorsList;

    public LogService() {
        this.log = new File(bomonitor.properties.getProperty("logFile"));
        BufferedReader freader=null;
        try {
            freader = new BufferedReader(new FileReader(bomonitor.properties.getProperty("errorList")));
            while (freader.ready()){
                String note = freader.readLine();
                int ind=note.indexOf('}');
                ;
                String value = note.substring(ind+1);
                errorsList.add(new ERROR(errorLevels.valueOf(note.substring(0,ind)),value));
                //errorsList.add(new ERROR( errorLevels.valueOf(freader.readLine()),freader.readLine()));
            }
            freader.close();
        } catch (FileNotFoundException e) {
            System.out.println("No list of errors found.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public String getMetric(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(log));
            while (reader.ready()){
                String geted = reader.readLine();
                for(ERROR e : errorsList){
                    if(geted.contains(e.getValue())) act(geted,e.getLevel());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("no log file");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No data for log service";
    }

    /**
     * Does some actions for all levels till current.
     * @param error
     * @param level
     */
    private void act(String error, errorLevels level){
        switch (level){
            case ignore:
                ;
            case trace:
                info();
                ;
            case monitor:
                info();
                ;
            case debug:
                info();
                ;
            case info:
                error();
                ;
            case warning:
                info();
                ;
            case error:
                warning();
                ;
            case critical:
                critical();
                break;

        }

    }
    void info(){
        System.out.println();

    }
    void debug(){}
    void critical(){}
    void error(){}
    void warning(){}
    void trace(){}

}
