package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
                serverSocket = new ServerSocket(10057);
                socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream os = socket.getOutputStream();
                while (socket.isConnected()){
                    String header = readHeader(in);
                    //System.out.println(in.readLine());
                    //System.out.println(in.readLine());
                    //System.out.println(in.readLine());
                }



        } catch(IOException e){
            e.printStackTrace();
        }

    }
    }

    String readHeader(BufferedReader br) throws IOException {
        String s = "";
        while (br.ready()){
            s=br.readLine();
        }
        System.out.println(s);
        return s;
    }

}
