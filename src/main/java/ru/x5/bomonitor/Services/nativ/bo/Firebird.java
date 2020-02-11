package ru.x5.bomonitor.Services.nativ.bo;

import ru.x5.bomonitor.Services.Metric;
import ru.x5.bomonitor.Services.nativ.ServiceNative;
import ru.x5.bomonitor.Services.StringMetric;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;

import java.io.File;
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
        String file_name = "/usr/local/gkretail/bo/data/standard_stamm.gdb.zip";
        File gdbFile = new File(file_name);
        if(gdbFile.exists()){
            res=1;
        }
        return res;
    }
    @StringMetric(value = "Репликация идет",directive = "native.firebird.stractual")
    public String StringIsActualGDB(){
        String res="";
        String file_name = "/usr/local/gkretail/bo/data/standard_stamm.gdb.zip";
        File gdbFile = new File(file_name);
        if(!gdbFile.exists()){
            res="Not actual GDB";
        }
        return res;
    }
}
