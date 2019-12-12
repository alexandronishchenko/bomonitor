package ru.x5.bomonitor.logsender.senders;

/**
 * Функциональный интерфейс для всех коннекторов отсылки.
 */
public interface SenderConnector {
    public boolean sendLine(String s);
}
