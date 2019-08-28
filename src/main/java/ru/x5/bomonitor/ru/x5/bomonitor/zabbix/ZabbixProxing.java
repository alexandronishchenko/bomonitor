package ru.x5.bomonitor.ru.x5.bomonitor.zabbix;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ZabbixProxing {
    private Socket connector;

    private static Logger loger=bomonitor.getLogger();
    public String sendRequestToClient(String req) throws IOException {
        String res ="";
        connector=new Socket();//"localhost",10050);
        //connector.setSoTimeout(2000);
        try {
            loger.insertRecord(this,"Trying to connect to localhost:10050",LogLevel.debug);
            connector.connect(new InetSocketAddress("localhost", 10050), 2000);
            //public void connect(SocketAddress endpoint, int timeout) throws IOException
            loger.insertRecord(this,"Connected to localhost:10050",LogLevel.debug);
            InputStreamReader is = new InputStreamReader(connector.getInputStream());
            loger.insertRecord(this,"InputStream fetched.",LogLevel.debug);
            OutputStream os = connector.getOutputStream();
            loger.insertRecord(this,"OutputStream fetched",LogLevel.debug);
            if (bomonitor.properties.getProperty("zabbix_version").equals("4")) {
                res = sendV4(req, os, is);
            } else if (bomonitor.properties.getProperty("zabbix_version").equals("3")) {
                loger.insertRecord(this,"Working with 3rd zabbix agent.",LogLevel.debug);
                res = sendV3(req, os, is);
            }
            is.close();
            os.close();
            connector.close();
        }catch (InterruptedIOException ie){
            loger.insertRecord(this,"Long answer from zab ag.",LogLevel.debug);
        }
        loger.insertRecord(this,"Result at send req to zabbix ag: "+res,LogLevel.debug);
        return res;
    }

    String sendV3(String req,OutputStream os,InputStreamReader is){
        String result=null;
        loger.insertRecord(this,"Sending "+req,LogLevel.debug);
        try {
            byte[] reqd = req.replaceAll("\u0000.*", "").replaceAll("\r", "").replaceAll("\n", "").getBytes();
            for (int i = 0; i < req.length(); i++) {
                os.write(reqd[i]);
            }
            os.write("\n".getBytes());
            os.flush();


            byte[] head = new byte[5];
            for (int i=0;i<5;i++){
                head[i]=(byte)is.read();
            }
            System.out.println(new String(head));
            loger.insertRecord(this,"Fetch head: "+new String(head),LogLevel.debug);
            byte[] b2 = new byte[8];
            for (int i = 0; i < 8; i++) {
                b2[i] = (byte) is.read();
            }
            ByteBuffer bb = ByteBuffer.wrap(b2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int lenth = bb.getInt();
            loger.insertRecord(this,"Fetch lenth: "+lenth,LogLevel.debug);
            if(lenth>0) {
                byte[] data1 = new byte[lenth];
                for (int i = 0; i < lenth; i++) {
                    data1[i] = (byte) is.read();
                }
                loger.insertRecord(this, "Fetch data: " + new String(data1), LogLevel.debug);
                result=new String(data1);
            }else {
                loger.insertRecord(this, "Lenth of mes: "+lenth, LogLevel.warn);
                loger.insertRecord(this, "Zabbix agent refuse the connection. "+lenth, LogLevel.warn);
                result=null;
            }

        }catch (IOException e){
            e.printStackTrace();
            loger.insertRecord(this,"err in sending v3 request",LogLevel.error);
        }
        return result;
    }
    String sendV4(String req,OutputStream os,InputStreamReader is){
        if(req.equals("") || req.isEmpty() || req==null){
            loger.insertRecord(this, "Null string will not be send. ", LogLevel.debug);
            return "";
        }
        loger.insertRecord(this, "Sending to agent :"+req, LogLevel.debug);
        String result=null;
        try {
            byte[] data = req.replaceAll("\u0000.*", "").replaceAll("\r", "").replaceAll("\n", "").getBytes();
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
            os.write(packet);
            os.flush();
            loger.insertRecord(this, "Fetching datafrom agent. ", LogLevel.debug);
            byte[] head = new byte[5];
            for (int i=0;i<5;i++){
                head[i]=(byte)is.read();
            }
            System.out.println(new String(head));
            loger.insertRecord(this, "Head: "+new String(head), LogLevel.debug);
            byte[] b2 = new byte[8];
            for (int i = 0; i < 8; i++) {
                b2[i] = (byte) is.read();
            }
            ByteBuffer bb = ByteBuffer.wrap(b2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int lenth = bb.getInt();
            loger.insertRecord(this, "Lenth : "+lenth, LogLevel.debug);
            if(lenth>0) {
                byte[] data1 = new byte[lenth];
                for (int i = 0; i < lenth; i++) {
                    data1[i] = (byte) is.read();
                }

                result = new String(data1);
                loger.insertRecord(this, "Data: " + new String(data1), LogLevel.debug);
            }else{
                result=null;
                loger.insertRecord(this, "Null data", LogLevel.warn);
            }
        }catch (IOException e){
            loger.insertRecord(this,"err in sending v4 request",LogLevel.error);
        }
        loger.insertRecord(this,result,LogLevel.debug);
        return result;
    }
//
//    String getResponse(InputStreamReader is) throws IOException {
//        byte[] b2 = new byte[8];
//        for (int i = 0; i < 8; i++) {
//            b2[i] = (byte) is.read();
//        }
//        ByteBuffer bb = ByteBuffer.wrap(b2);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        int lenth = bb.getInt();
//        byte[] data = new byte[lenth];
//        for (int i = 0; i < lenth; i++) {
//            data[i] = (byte) is.read();
//        }
//        return new String(data);
//    }


}
