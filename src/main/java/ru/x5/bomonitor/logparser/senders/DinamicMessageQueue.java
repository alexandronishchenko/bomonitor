package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.bomonitor;

import java.io.*;
import java.util.*;

public class DinamicMessageQueue {
    private SenderCache senderCache;


    public DinamicMessageQueue() {
        senderCache=SenderCache.getInstance();
    }


    public void sendFirst(){
        senderCache.sendFirst();
        }





}
