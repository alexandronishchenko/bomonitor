package ru.x5.bomonitor.logparser.senders;

/**
 * Печать в аут. Используется всегда, дополнительно включается в параметрах.
 * log.consumers=out
 */
public class OutSenderConnector implements SenderConnector {
    @Override
    public boolean sendLine(String s) {
        System.out.println("Sended string: -> "+s);
        return true;
    }
}
