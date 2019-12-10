package ru.x5.bomonitor.logparser.senders;

public class Sender implements Runnable {
    private boolean running=false;
    private DinamicMessageQueue messageQueue;

    @Override
    public void run() {
        this.running=true;
        messageQueue=new DinamicMessageQueue();
        while (this.running){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            trySend();
        }
        running=false;
    }

    private void trySend(){
        messageQueue.sendFirst();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
