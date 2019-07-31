package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import ru.x5.bomonitor.ZQL.Composer;

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
                    readHeader(in);
                    String div = getCommand(in);
                    sendResponse(os,div);
                    in.close();
                    os.close();
                    socket.close();


               // }
        } catch(IOException e){
                this.isRun=false;
                //socket.close();
                System.out.println("Exc");
                Thread.currentThread().interrupt();
            e.printStackTrace();
        }
//            finally {
//
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    System.out.println("Unable to close server socket.");
//                    //e.printStackTrace();
//                }
//            }

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
        //System.out.println(new String(head));
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
            return new String(data);
    }
    public void sendResponse(OutputStream ou,String directive) throws IOException {
//        SyncJob job = new SyncJob();
//        ArrayList<String> comands = getServiceParams(directive);
//        for(String s : comands){
//            job.addDirective(s);
//        }
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
//    ArrayList<String> getServiceParams(String s){
//        ArrayList<String> result=new ArrayList<>();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < s.length(); i++) {
//            if(s.charAt(i)=='.'){
//                result.add(s.substring(0,i));
//                s=s.substring(i+1);
//            }
//        }
//        result.add(s);
//        return result;
//    }
    public void closeServSocket(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
