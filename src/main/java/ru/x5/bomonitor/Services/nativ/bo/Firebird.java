package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

@ServiceNative("Firebird")
public class Firebird extends ParrentNativeService {
    public Firebird() {
        this.name="firebird";
        this.value="";
    }

    @Override
    public String get(String directive) {
        String result= "";
        if(directive.equals("actual")){
            result=String.valueOf(isActualGDB());
        }else if(directive.equals("stractual")){
            result=StringIsActualGDB();
        }
        return result;
    }

    @Override
    public String get(String directive, String subquery) {
        return "";
    }
@Metric(value = "Репликация идет число",directive = "native.firebird.actual")
    public int isActualGDB(){
        int res=0;
        boolean b=false;
        String file_name = "/usr/local/gkretail/bo/data/standard_stamm.gdb.zip";
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(Paths.get(file_name), BasicFileAttributes.class);
            String created_date= String.valueOf(attr.creationTime());
            long dt = new Date().getTime()-(24*60*60*1000);
            SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
            String date = smp.format(new Date(dt));
            b=date.equals(created_date.substring(0,10));
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("No file GDB");
            return 0;
        }
        res=b?0:1;
        return res;
    }
    @StringMetric(value = "Репликация идет",directive = "native.firebird.stractual")
    public String StringIsActualGDB(){
        String res="";
        boolean b=false;
        String file_name = "/usr/local/gkretail/bo/data/standard_stamm.gdb.zip";
        BasicFileAttributes attr = null;
        try {
            attr = Files.readAttributes(Paths.get(file_name), BasicFileAttributes.class);
            String created_date= String.valueOf(attr.creationTime());
            long dt = new Date().getTime()-(24*60*60*1000);
            SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd");
            String date = smp.format(new Date(dt));
            b=date.equals(created_date.substring(0,10));
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("No file GDB");
            return "No file GDB";
        }
        res=b?"":"Not actual GDB";
        return res;
    }
}
