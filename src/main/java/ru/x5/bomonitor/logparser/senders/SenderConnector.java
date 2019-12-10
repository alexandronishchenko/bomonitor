package ru.x5.bomonitor.logparser.senders;

public interface SenderConnector {
    public boolean sendLine(String s);
}
