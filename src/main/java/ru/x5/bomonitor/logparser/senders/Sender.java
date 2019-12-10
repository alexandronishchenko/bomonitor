package ru.x5.bomonitor.logparser.senders;

public class Sender implements Runnable {
    private boolean running=false;
    private DinamicMessageQueue messageQueue;

    @Override
    public void run() {
        running=true;
        messageQueue=new DinamicMessageQueue();
        while (running){
            trySend();
        }
        running=false;
    }

    private void trySend(){
        messageQueue.sendFirst();
    }

}
