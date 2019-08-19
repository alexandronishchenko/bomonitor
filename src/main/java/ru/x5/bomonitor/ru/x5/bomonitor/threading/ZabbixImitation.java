package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.ZQL.Composer;
import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ZabbixImitation implements Runnable{
    private Boolean isRun=true;

    private int port;
    public ZabbixImitation(){

    }
    public ZabbixImitation(int port) {
        this.port=port;
        this.isRun=true;
    }

    public void setRun(Boolean run) {
        isRun = run;
    }
    public boolean getRun(){
        return this.isRun;
    }

    ServerSocket serverSocket;
    Socket socket;
    InputStreamReader in;
    OutputStream os;

    @Override
    public void run() {
        System.out.println("Version of listening zabbix server: "+bomonitor.properties.getProperty("zabbix_version"));
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            this.isRun=false;
            e.printStackTrace();
        }
        try {
            serverSocket.setReuseAddress(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (!serverSocket.isClosed()) {

            try {
                //while (true){

                    //serverSocket.setSoTimeout(10);//
                    socket = serverSocket.accept();
                    socket.setReuseAddress(true);
                    in = new InputStreamReader(socket.getInputStream());
                    os = socket.getOutputStream();
                    if(bomonitor.properties.getProperty("zabbix_version").equals("4")){//for 4-th zabbix server
                        readHeader(in);
                        String div = getCommand4v(in);
                        sendResponse(os,div);
                        in.close();
                        os.close();
                        socket.close();
                    }else if(bomonitor.properties.getProperty("zabbix_version").equals("3")){//for 3-rd zabbix server
                        String div = getCommand3v(in);
                        sendResponse(os,div);
                        in.close();
                        os.close();
                        socket.close();
                    }


               // }
        } catch(IOException e){
                this.isRun=false;
            bomonitor.getLogger().insertRecord("Socket was closed.", LogLevel.error);
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readHeader(InputStreamReader br) throws IOException {
        byte[] head = new byte[5];
        for (int i=0;i<5;i++){
            head[i]=(byte)br.read();
        }
        System.out.println(new String(head));
    }
    public String getCommand4v(InputStreamReader br) throws IOException {
            byte[] b2 = new byte[8];
            for (int i = 0; i < 8; i++) {
                b2[i] = (byte) br.read();
            }
            ByteBuffer bb = ByteBuffer.wrap(b2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int lenth = bb.getInt();
            byte[] data = new byte[lenth];
            for (int i = 0; i < lenth; i++) {
                data[i] = (byte) br.read();
            }
            return new String(data);
    }
    public String getCommand3v(InputStreamReader br) throws IOException {
        byte[] dat=new byte[1024];
        int i=0;
        while (br.ready()){
            //data+=String.valueOf(br.read());
            dat[i]= (byte) br.read();
            i++;
        }

        String data = new String(dat).replaceAll("\u0000.*", "").replaceAll("\r","").replaceAll("\n","");
        //System.out.println(data);
        return data;
    }
    public void sendResponse(OutputStream ou,String directive) throws IOException {
        if(directive.isEmpty() || directive==null){
            bomonitor.getLogger().insertRecord("Null string was fetched.",LogLevel.error);
            return;
        }
        Composer composer = new Composer(directive);
        String result = String.valueOf(composer.getResult());
        byte[] data = result.getBytes();
        byte[] header = new byte[] {
                'Z', 'B', 'X', 'D', '\1',
                (byte)(data.length & 0xFF),
                (byte)((data.length >> 8) & 0xFF),
                (byte)((data.length >> 16) & 0xFF),
                (byte)((data.length >> 24) & 0xFF),
                '\0', '\0', '\0', '\0'};

        byte[] packet = new byte[header.length + data.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(data, 0, packet, header.length, data.length);
        ou.write(packet);
        ou.flush();

    }

    public void closeServSocket(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
