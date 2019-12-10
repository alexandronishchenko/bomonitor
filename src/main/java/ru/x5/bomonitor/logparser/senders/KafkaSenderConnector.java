package ru.x5.bomonitor.logparser.senders;

public class KafkaSenderConnector implements SenderConnector {
    @Override
    public boolean sendLine(String s) {
        //implement and inject kafka from kafka package.
        return false;
    }
}
