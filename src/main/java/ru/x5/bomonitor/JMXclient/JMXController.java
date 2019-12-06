package ru.x5.bomonitor.JMXclient;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.ServiceController;
import ru.x5.bomonitor.Services.ZQL.JMXservice;
import ru.x5.bomonitor.bomonitor;

@ServiceController(name = "JMX client")
public class JMXController {
    Logger logger = bomonitor.getLogger();
    public JMXController() {
        logger.insertRecord(this,"JMX clien service initialized.",LogLevel.debug);
    }

    public static JMXservice getJMXCli(){
        return new JMXservice();
    }

}
