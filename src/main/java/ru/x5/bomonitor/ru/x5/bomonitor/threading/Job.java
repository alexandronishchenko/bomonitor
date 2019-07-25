package ru.x5.bomonitor.ru.x5.bomonitor.threading;

public class Job implements Runnable {
    private Boolean isRun;

    public Job() {
        this.isRun=false;
    }
    public Job(Boolean b) {
        this.isRun=b;
    }

    public void run() {
        while(isRun){

        }


    }
    public void setRun(Boolean b){
        this.isRun=b;
    }
}
