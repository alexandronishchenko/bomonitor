package ru.x5.bomonitor.zabbix;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.Services.ZQL.Composer;
import ru.x5.bomonitor.bomonitor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ZabbixAgentThread extends Thread {

    private Socket socket;
    private Logger loger = bomonitor.getLogger();
    private InputStreamReader in;
    private OutputStream os;

    public ZabbixAgentThread(Socket socket) {
        this.socket = socket;
        try {
            this.socket.setReuseAddress(true);
            this.socket.setSoTimeout(10000);
            in = new InputStreamReader(socket.getInputStream());
            os = socket.getOutputStream();
        } catch (SocketException e) {
            loger.insertRecord(this,"Error with starting thred agent.", LogLevel.error);
            e.printStackTrace();
        } catch (IOException e) {
            loger.insertRecord(this,"Cant get stream from socket.", LogLevel.error);
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        while (!this.socket.isClosed() && !this.isInterrupted()){
            try {
                if (bomonitor.properties.getProperty("zabbix_version").equals("4")) {//for 4-th zabbix server
                    readHeader(in);
                    String div = getCommand4v(in);
                    sendResponse(os, div);
                    in.close();
                    os.close();
                    socket.close();
                } else if (bomonitor.properties.getProperty("zabbix_version").equals("3")) {//for 3-rd zabbix server
                    String div = getCommand3v(in);
                    sendResponse(os, div);
                    in.close();
                    os.close();
                    socket.close();
                }
            }catch (IOException e){
                loger.insertRecord(this,"Error in messaging between agent and server.", LogLevel.error);
            }
        }
        ZabbixAgentServer.threads.remove(this);

    }
    void readHeader(InputStreamReader br) throws IOException {
        byte[] head = new byte[5];
        for (int i = 0; i < 5; i++) {
            head[i] = (byte) br.read();
        }
        loger.insertRecord(this, new String(head), LogLevel.debug);
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
        loger.insertRecord(this, new String(data), LogLevel.debug);
        return new String(data);
    }

    public String getCommand3v(InputStreamReader br) throws IOException {
        byte[] dat = new byte[1024];
        int i = 0;
        while (br.ready()) {
            //data+=String.valueOf(br.read());
            dat[i] = (byte) br.read();
            i++;
        }

        String data = new String(dat).replaceAll("\u0000.*", "").replaceAll("\r", "").replaceAll("\n", "");
        //System.out.println(data);
        loger.insertRecord(this, "Fetched command: " + data, LogLevel.debug);
        return data;
    }

    public void sendResponse(OutputStream ou, String directive) throws IOException {
        if (directive.isEmpty() || directive == null) {
            loger.insertRecord(this, "Null string was fetched. Closing connection.", LogLevel.warn);
            return;
        }
        Composer composer = new Composer(directive);
        String result = String.valueOf(composer.getResult());
        if (result.equals("null")) {
            loger.insertRecord(this, "Null result. Sending empty row.", LogLevel.debug);
            //return;
            result = "";
        }
        byte[] data = result.getBytes();
        byte[] header = new byte[]{
                'Z', 'B', 'X', 'D', '\1',
                (byte) (data.length & 0xFF),
                (byte) ((data.length >> 8) & 0xFF),
                (byte) ((data.length >> 16) & 0xFF),
                (byte) ((data.length >> 24) & 0xFF),
                '\0', '\0', '\0', '\0'};

        byte[] packet = new byte[header.length + data.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(data, 0, packet, header.length, data.length);
        ou.write(packet);
        ou.flush();

    }
}
