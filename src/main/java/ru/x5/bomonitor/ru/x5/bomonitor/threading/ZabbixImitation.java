package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class ZabbixImitation implements Runnable{
    private Boolean isRun=true;

    public Boolean getRun() {
        return isRun;
    }

    public void setRun(Boolean run) {
        isRun = run;
    }

    //Socket socket;
    ServerSocket serverSocket;

    @Override
    public void run() {
        while (isRun) {
            Socket socket;
            try {

                while (true){
                    serverSocket = new ServerSocket(10057);
                    socket = serverSocket.accept();
                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    OutputStream os = socket.getOutputStream();
                    readHeader(in);

                        String div = getCommand(in);
                        sendResponse(os,div);


//                    while (socket.isConnected()){
//                       System.out.println(getCommand(in));
//                    }
                    in.close();
                    os.close();
                    socket.close();//??
                    serverSocket.close();
                }




        } catch(IOException e){
            e.printStackTrace();
        }

    }
    }

    void readHeader(InputStreamReader br) throws IOException {
        byte[] head = new byte[5];
        for (int i=0;i<5;i++){
            head[i]=(byte)br.read();
        }
        System.out.println(new String(head));
    }
    public String getCommand(InputStreamReader br) throws IOException {
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
        //System.out.println(new String(data));
        //System.out.println(br.read());
            return new String(data);
    }
    public void sendResponse(OutputStream ou,String directive) throws IOException {
        //TODO: method to send data to zabbix.
//
//        String[] directives = getServiceParams(directive);
//        Job job = new Job();
//        System.out.println("Directives l: "+directives.length);
//        for(String s : directives){
//            job.addDirective(s);
//
//        }
//
//        int result=job.runJob();
//        BigInteger integ = BigInteger.valueOf(result);
//        byte[] msg = integ.toByteArray();
//
//        byte[] header1 = new byte[]{'Z', 'B', 'X', 'D', '\1',};
//        for (int i = 0; i < header1.length; i++) {
//            ou.write(header1[i]);
//        }
//        BigInteger integ1 = BigInteger.valueOf(msg.length);
//        byte[] lenth = integ1.toByteArray();
//        byte[] length = new byte[] {//change!
//                (byte)(lenth.length & 0xFF),
//                (byte)((lenth.length >> 8) & 0xFF),
//                (byte)((lenth.length >> 16) & 0xFF),
//                (byte)((lenth.length >> 24) & 0xFF),
//                '\0', '\0', '\0', '\0'};
//       // byte[] length=new byte[]{1,0,0,0,0,0,0,0};
//        System.out.println(lenth.length+"->");
//        for (int i = 0; i <8 ; i++) {
//            ou.write(length[i]);
//            System.out.println(length[i]);
//        }
//        for (int i = 0; i < msg.length; i++) {
//            ou.write(msg[i]);
//        }
//        ou.flush();


//        System.out.println("dircect");
//        String[] directives = directive.split(".");
//        Job job = new Job();
//        for(String s : directives){
//            job.addDirective(s);
//            System.out.println(s);
//        }
//
//
//        BigInteger integ = BigInteger.valueOf(2);
//        byte[] data = integ.toByteArray();
//        byte[] header = new byte[] {
//                'Z', 'B', 'X', 'D', '\1',
//                (byte)(data.length & 0xFF),
//                (byte)((data.length >> 8) & 0xFF),
//                (byte)((data.length >> 16) & 0xFF),
//                (byte)((data.length >> 24) & 0xFF),
//                '\0', '\0', '\0', '\0'};
//
//        byte[] packet = new byte[header.length + data.length];
//        System.arraycopy(header, 0, packet, 0, header.length);
//        System.arraycopy(data, 0, packet, header.length, data.length);
//        for (int i = 0; i <packet.length ; i++) {
//            ou.write(packet[i]);
//        }
//        ou.flush();

        BigInteger bi = BigInteger.valueOf(1);
        byte[] data = bi.toByteArray();
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
//        for (int i = 0; i <packet.length ; i++) {
//            ou.
//        }
        ou.write(packet);
        ou.flush();

    }

    String[] getServiceParams(String s){
        System.out.println(s);
        String[] arr = s.toString().split(".");
        return arr;
    }

}
