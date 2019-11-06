package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.database.*;
import ru.x5.bomonitor.database.Entity.ItemPrice;
import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.database.Entity.POS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
@ServiceNative("Контроль цен")
public class Prices extends ParrentNativeService {
    Logger logger = bomonitor.getLogger();
    public Prices() {
        this.name="prices";
    }

    @Override
    public String get(String directive) {
        String result="";
        try {
            if(directive.equals("errorchange")){
                    result= String.valueOf(getErrorChange());
            }else if(directive.equals("strerrorchange")) {
                result= getStringErrorChange();
            }else if(directive.equals("posbodifference")) {
                result= getPosBoDifference();
            }
        }
         catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }


    @Metric("ошибки в изменении цен")
    public int getErrorChange() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        int subres1=0;
        try {
            subres1 = Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_PRICE_GK,"count",new String[]{date}).get("count")) +
                    Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_PRICE_CHANGE,"count",new String[]{date}).get("count"));
        }catch (NumberFormatException e){
            System.out.println("NULL returned at errors");
        }
        int subres2=0;
        try {
            subres2 = Integer.parseInt(PostgresConnection.executeSelect(PostgresSQLqueries.COUNT_UNPRINTED).get("count"));
        }catch (NumberFormatException e){
            System.out.println("NULL at 3-rd query PRICES.");
        }
        return subres1+subres2;
    }
    @StringMetric("ошибки в изменении цен")
    public String getStringErrorChange() throws SQLException {
        long dt = new Date().getTime()-(10*24*60*60*1000);
        SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
        String date = smp.format(new Date(dt));
        String result="";
        String s1 = PostgresConnection.executeSelect(PostgresSQLqueries.PRICE_GK_ERR,"item_id",new String[]{date}).get("item_id");
        if(s1!=null&&!s1.equals("null")&&!s1.equals("NULL"))result+=s1;
        String s2= PostgresConnection.executeSelect(PostgresSQLqueries.PRICE_CHANGE_ERR,"item_id",new String[]{date}).get("item_id");
        if(s2!=null&&!s2.equals("null")&&!s1.equals("NULL"))result+=s2;
        String s3 = PostgresConnection.getNote(PostgresSQLqueries.UNPRINTED_PRICES).get("item_id");
        if(s3!=null&&!s3.equals("null")&&!s1.equals("NULL"))result+=s3;
        return result;
    }

    @StringMetric("Разница цен на кассах и БО")
    public String getPosBoDifference() throws SQLException {
        //получаем эталонный лист цен из БО.
        Table<ItemPrice> itemPricesTableBO = PostgresConnection.executeTableSelectPrices(PostgresSQLqueries.ITEM_SELLING_PRICES);
//TODO добавить определение касс, получение листов с каждой кассы.
        //Определяем количество касс:
        ArrayList<POS> poses = new ArrayList<>();
   //     poses.add(0,new POS("localhost"));
        for(int i=1;i<10;i++){
            try {
                InetAddress adr = InetAddress.getByName("POS0"+i);
                if(adr.isReachable(5000)){
                    System.out.println("POS0"+i+" was found and is reachable.");
                    poses.add(new POS("POS0"+i));
                }
            } catch (UnknownHostException e) {
                logger.insertRecord(this,"No host with name POS0"+i,LogLevel.debug);
                e.printStackTrace();
            } catch (IOException e) {
                logger.insertRecord(this,"Host POS0"+i+" unreachable.",LogLevel.debug);
                e.printStackTrace();
            }
        }
        //Получаем данные из БД касс.
        for(POS pos : poses) {
            System.out.println(pos.getName()+" creating DB connection...");
            FirebirdConnection fbConnection = new FirebirdConnection(pos.getName());
            pos.setPrices(fbConnection.executeTableSelectPrices(FirebirdSQLqueries.ITEM_SELLING_PRICES));
        }
        //Сравниваем результаты цен касс и БО. Записывая ошибки в строку ERROR.
        String ERROR="";
        //Проверка по размеру.
        logger.insertRecord(this,"Сверка размера баз.",LogLevel.debug);
        for(POS pos : poses){
            System.out.println(pos.getName()+" size is"+pos.getPrices().getList().size()+", size at BO: "+itemPricesTableBO.getList().size());
            if(pos.getPrices().getList().size()!=itemPricesTableBO.getList().size()){
                ERROR+=pos.getName()+" size different: "+pos.getPrices().getList().size()+" at pos, but"+ itemPricesTableBO.getList().size()+" at BO.";
            }
        }
        //поштучная проверка для каждой ПЛЮ.
        for(int i =0;i<itemPricesTableBO.getList().size();i++){
            logger.insertRecord(this,"Item: "+itemPricesTableBO.getList().get(i).getItemId()+"Type Code:"+itemPricesTableBO.getList().get(i).getPriceType()+", amount:"+
                    +itemPricesTableBO.getList().get(i).getPrice(),LogLevel.debug);
            for(POS pos : poses){
                if(!pos.getPrices().getList().contains(itemPricesTableBO.getList().get(i))){
                    ERROR+=" "+pos.getName()+" does not have ITEM: "+itemPricesTableBO.getList().get(i).getItemId()+" with "+
                            itemPricesTableBO.getList().get(i).getPriceType()+"type code and amount: "+itemPricesTableBO.getList().get(i).getPrice();
                }else{
                    logger.insertRecord(this,pos.getName()+" no such itemPrice...", LogLevel.warn);
                }
            }

        }
        return ERROR;
    }

}
