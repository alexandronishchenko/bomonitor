package ru.x5.bomonitor;

import org.junit.Before;
import org.junit.Test;
import ru.x5.bomonitor.Services.ServiceInterface;
import ru.x5.bomonitor.Services.ZQL.Action;
import ru.x5.bomonitor.Services.nativ.bo.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class bomonitorTest {

    @Before
    public void setUp() throws Exception {
        File test = new File("/etc/zabbix/bomonitor/bomonitor.properties");
        if(!test.exists()){
            throw new Exception();
        }
    }
    @Test
    public void initialize() throws Exception {
         HashMap<String, ServiceInterface> mapping = new HashMap<>();

            mapping.put("loyalty", new Loyalty());
            mapping.put("db", new DBMonitoring());
            mapping.put("egais", new EGAIS());
            mapping.put("prices", new Prices());
            mapping.put("items", new Items());
            mapping.put("printers", new Printers());
            mapping.put("reciepts", new Reciepts());
            mapping.put("stock", new Stock());
            mapping.put("taskmanager", new Taskmanager());
            mapping.put("transportmodule", new TransportModule());
            mapping.put("firebird", new Firebird());
            //mapping.put("action", new Action());
        HashMap<String,ServiceInterface> map =bomonitor.initialize();
        if(map.size()!=mapping.size()) throw new Exception();
        for(Map.Entry<String,ServiceInterface> pr : mapping.entrySet()){
            if(!mapping.containsKey(pr.getKey())&&!mapping.containsValue(pr.getValue())) throw new Exception();
        }
    }

    @Test
    public void printAllMetrics() throws Exception {
        ArrayList<String> info = new ArrayList<>();
        info.add("fulldiag or fulldiag.db(reciepts ...)");
        info.add("log.bolog or log.postgreslog or log.bolog.error");
        info.add("native.firebird.actual");
        info.add("native.egais.gettmptables");
        info.add("native.items.getdiff");
        info.add("native.db.activerequests");
        info.add("native.db.frozentransaction");
        info.add("native.db.long");
        info.add("native.db.tmptables");
        info.add("native.prices.errorchange");
        info.add("native.printers.queue");
        info.add("native.reciepts.balancediff");
        info.add("native.reciepts.duplicatebon");
        info.add("native.reciepts.incorrectbon");
        info.add("native.reciepts.queue");
        info.add("native.reciepts.stockandreciept");
        info.add("native.stock.geterrors");
        info.add("native.taskmanager.count");
        info.add("native.transportmodule.geterrors");
        info.add("jmx.heap.HeapMemoryUsage.used");
        info.add("jmx.gc1.CollectionCount");
        info.add("jmx.gc2.CollectionCount");
        info.add("jmx.classesloaded.LoadedClassCount");
        info.add("jmx.activemq.TotalMessageCount");
        info.add("jmx.defactivemq.QueueSize");
        info.add("jmx.threads.ThreadCount");
        info.add("jmx.openedfiles.OpenFileDescriptorCount");
        ArrayList<String> mainInfo = bomonitor.printAllMetrics();
        if(info.size()!=mainInfo.size()) throw new Exception();
    }

    @Test
    public void getLogger() {
    }
}