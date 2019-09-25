package ru.x5.bomonitor.Services.ZQL;


import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.bomonitor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Класс для нативных проверок (БД, файлы).
 */
public class NativeService extends Service {
    ArrayList<String> directives;

    public void setDirectives(ArrayList<String> directives) {
        this.directives = directives;
    }

    /**
     * Логер из главного класса.
     */
    private static Logger loger=bomonitor.getLogger();
    /**
     * Карта всех нативных сервисов. Инициализируется рефлексией из основного класса. Важно, чтобы имя класса соответствовало метрике! То есть для метрик сервиса БД класс DB, значит
     * метрика должна быть в заббиксе native.{HOST_TYPE}.db.{metric_mapped_method}
     */
    static HashMap<String, ServiceNativeInterface> mapping = bomonitor.initializeNativeServices();

    /**
     * Получение результата. В случае ошибки возвращает null.
     * @return строку со значением метрики.
     */
    @Override
    public String getMetric(){
        String result =null;
        String subquery=null;
        String service=null;
        String param=null;
      try{
          if(directives.size()==4){//3 param
            service=directives.get(1);
            param=directives.get(2);
            subquery=directives.get(3);
            result = mapping.get(service).get(param, subquery);
        }else if(directives.size()==3){//2 param
            service=directives.get(1);
            param=directives.get(2);
            result= mapping.get(service).get(param);
        }else {//else count of param
              loger.insertRecord(this,"incorrect param",LogLevel.info);
        }
    }catch (NullPointerException e){
          loger.insertRecord(this,"No such service or result was null: "+ service+" -> "+param,LogLevel.warn);
          //loger.insertRecord(this,e.getMessage(), LogLevel.error);
    }
        return result;

    }
    private String getDate(int hours){
        long dt = new Date().getTime()-(hours*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        return smp.format(new Date(dt));
    }
    private String[] getQueries(){
        String queries=this.directives.get(1);
        return queries.split("&");
    }

}
