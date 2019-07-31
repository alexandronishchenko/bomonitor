package ru.x5.bomonitor.Services.nativ;

import ru.x5.bomonitor.Services.Service;
import ru.x5.bomonitor.Services.ServiceUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

@ServiceUnit
public class Firebird implements Service {
    @Override
    public int get(String directive) {
        int result= 0;
        if(directive.equals("actual")){
            result=isActualGDB()?1:0;
        }
        return result;
    }

    @Override
    public int get(String directive, String subquery) {
        return 0;
    }

    boolean isActualGDB(){
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
            e.printStackTrace();
        }
        return b;
    }
}
