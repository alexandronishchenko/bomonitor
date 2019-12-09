package ru.x5.bomonitor.logparser.consumers;

public class OutConsumer implements Consumer {
    @Override
    public void sendLine(String s) {
        System.out.println(s);
    }
}
