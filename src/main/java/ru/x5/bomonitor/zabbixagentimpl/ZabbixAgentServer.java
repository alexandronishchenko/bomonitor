package ru.x5.bomonitor.zabbixagentimpl;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.ServiceController;
import ru.x5.bomonitor.bomonitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Zabbix agent implementation. Listen port from properties. And run threads for each connection.
 */
@ServiceController(name="Zabbix agent service controller.")
public class ZabbixAgentServer implements Runnable {
    private static Logger loger = bomonitor.getLogger();
    private Boolean isRun = true;

    private int port;

    public ZabbixAgentServer() {

    }

    public ZabbixAgentServer(int port) {
        this.port = port;
        this.isRun = true;
    }

    public void setRun(Boolean run) {
        isRun = run;
    }

    public boolean getRun() {
        return this.isRun;
    }

    ServerSocket serverSocket;
    static ArrayList<Thread> threads = new ArrayList<>();

    @Override
    public void run() {
        this.isRun=true;
        loger.insertRecord(this,"Zabbix-agent implimentation service started.",LogLevel.debug);
        System.out.println("Version of listening zabbix server: " + bomonitor.properties.getProperty("zabbix_version"));
        try {
            serverSocket = new ServerSocket(this.port);
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(180000);
        } catch (IOException e) {
            this.isRun = false;
            loger.insertRecord(this, e.getMessage(), LogLevel.error);
            e.printStackTrace();
        }
//        try {
//
////        } catch (SocketException e) {
////            e.printStackTrace();
////        }
        while (!serverSocket.isClosed()) {

            try {
                Socket socket;
                socket = serverSocket.accept();
                ZabbixAgentThread newThread = new ZabbixAgentThread(socket);
                threads.add(newThread);
                newThread.start();
            } catch (IOException e) {
                this.isRun = false;
                bomonitor.getLogger().insertRecord(this, "Socket was closed.", LogLevel.info);
                Thread.currentThread().interrupt();
                //e.printStackTrace();
            }
        }
        try {

            for (int i = 0; i < threads.size(); i++) {
                threads.get(i).interrupt();
                threads.remove(i);
            }
            serverSocket.close();
        } catch (IOException e) {
            this.isRun = false;
            e.printStackTrace();
        }
    }


}
