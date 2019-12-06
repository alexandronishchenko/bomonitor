package ru.x5.bomonitor.logparser;


import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.ServiceController;
import ru.x5.bomonitor.bomonitor;

/**
 * Service control log parsing threads. Singletone like zabbix-service thread.
 */
@ServiceController(name="Log monitor controller")
public class LogService {

    LogService instance;
    Logger logger;

    private LogService() {
        logger= bomonitor.getLogger();
    }
    public LogService getInstance(){
        if(instance==null){
            instance = new LogService();
        }
        return instance;
    }



}
