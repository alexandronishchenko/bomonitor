package ru.x5.bomonitor.ru.x5.bomonitor.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ZabbixProxing {
    private Socket connector;

    public String sendRequestToClient(String req) throws IOException {
        String res ="";

            connector=new Socket("localhost",10050);
            InputStreamReader is = new InputStreamReader(connector.getInputStream());
            OutputStream os=connector.getOutputStream();
            //v3:
            byte[] reqd = req.getBytes();
            for(int i=0;i<req.length();i++){
                os.write(reqd[i]);
            }
            os.write("\n".getBytes());
            os.flush();
            os.close();
            byte[] resp = new byte[1024];
            int counter=0;
            while(is.ready()){
                resp[counter]=(byte)is.read();
                counter++;
            }
            res=new String(resp).replaceAll("\u0000.*", "").replaceAll("\r","").replaceAll("\n","");



        return res;
    }

    String getResponse(InputStreamReader is) throws IOException {
        byte[] b2 = new byte[8];
        for (int i = 0; i < 8; i++) {
            b2[i] = (byte) is.read();
        }
        ByteBuffer bb = ByteBuffer.wrap(b2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int lenth = bb.getInt();
        byte[] data = new byte[lenth];
        for (int i = 0; i < lenth; i++) {
            data[i] = (byte) is.read();
        }
        return new String(data);
    }


}
