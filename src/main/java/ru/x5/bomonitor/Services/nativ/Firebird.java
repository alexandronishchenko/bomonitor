package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.Metric;
import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;
import ru.x5.bomonitor.StringMetric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

@ServiceUnit("Firebird")
public class Firebird implements Service {
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
@Metric("Репликация идет")
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
    @StringMetric("Репликация идет")
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
