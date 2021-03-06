package ru.x5.bomonitor.Services.ZQL;

import ru.x5.bomonitor.Services.logmonitoring.ERROR;
import ru.x5.bomonitor.Services.logmonitoring.errorLevels;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.Services.ZQL.Service;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//TODO: save lenth of file. And bytebuffered read from the end. But it should have variable to save the lenth. May be better on zabbix native agent.
public class LogService extends Service {
    File log=null;
    ArrayList<ERROR> errorsList=new ArrayList<>();

    public LogService() {

        BufferedReader freader=null;
        try {
            freader = new BufferedReader(new FileReader(bomonitor.properties.getProperty("errorList")));
            while (freader.ready()){
                String note = freader.readLine();
                int ind=note.indexOf('}');
                String value = note.substring(ind+2,note.length()-1);
                String lev=note.substring(2,ind).toLowerCase();
                errorsList.add(new ERROR(errorLevels.valueOf(lev),value));
            }
            freader.close();
        } catch (FileNotFoundException e) {
            System.out.println("No list of errors found.");
        } catch (IOException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            System.out.println("Incorrect value at error list.");
        }

    }


    @Override
    public String getMetric(){
        this.log = new File(bomonitor.properties.getProperty(directives.get(1)));
        String result="";
        if(directives.size()==2){//return all errors from log, which are at list
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.log));
                while (reader.ready()){
                    String geted = reader.readLine();
                    //System.out.println(geted);
                    for(ERROR e : errorsList){
                        if(geted.contains(e.getValue())){
                            result+=act(geted,e.getLevel())+" ";
                        }
                    }
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("no log file");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if(directives.size()==3){//return all errors from log, which are at list and is required level
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.log));
                while (reader.ready()){
                    String geted = reader.readLine();
                    for(ERROR e : errorsList){
                        if(geted.contains(e.getValue())
                                && e.getLevel().toString().equals(directives.get(3))){
                            result+=act(geted,e.getLevel())+" ";
                            //result+=geted;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("no log file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            return "Error log metric";
        }


        return result;
    }

    /**
     * Does some actions for all levels till current.
     * @param error
     * @param level
     */
    private String act(String error, errorLevels level){//one by one actions for different errors. Now has no actions. (at zabbix).
        //System.out.println("Start:"+error+"->"+level.toString().toUpperCase());
        String result="";
        if(checkDate(error))result=error;
        switch (level){
            case ignore:
                ;info();
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
                critical(error);
                break;

        }
        return result=error+"->"+level.toString().toUpperCase()+"\n";

    }

    private boolean checkDate(String s){
        boolean isCorrect=false;
        //Date current_date=new Date();
        long curdat=new Date().getTime()-60*60*1000;
        SimpleDateFormat sd = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:");
        String date = sd.format(new Date(curdat));


        //2019-06-28 16:11:04:818+0300



        return isCorrect;
    }

    void info(){
        System.out.println();

    }
    void debug(){}
    void critical(String error){}
    void error(){}
    void warning(){}
    void trace(){}

}
