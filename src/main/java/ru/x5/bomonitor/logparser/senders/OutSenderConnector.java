package ru.x5.bomonitor.logparser.senders;

public class OutSenderConnector implements SenderConnector {
    @Override
    public boolean sendLine(String s) {
        System.out.println(s);
        return true;
    }
}
