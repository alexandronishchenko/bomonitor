package ru.x5.bomonitor.zabbix;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.Services.ZQL.Composer;
import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

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
    ArrayList<Thread> threads=new ArrayList<>();

    @Override
    public void run() {
        System.out.println("Version of listening zabbix server: " + bomonitor.properties.getProperty("zabbix_version"));
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            this.isRun = false;
            loger.insertRecord(this, e.getMessage(), LogLevel.error);
            e.printStackTrace();
        }
        try {
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(30000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (!serverSocket.isClosed()) {

            try {
                Socket socket;
                socket = serverSocket.accept();
                ZabbixAgentThread newThread = new ZabbixAgentThread(socket);
                threads.add(newThread) ;
                newThread.start();
            } catch (IOException e) {
                this.isRun = false;
                bomonitor.getLogger().insertRecord(this, "Socket was closed.", LogLevel.info);
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        try {
            for(Thread th : this.threads){
                th.interrupt();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
