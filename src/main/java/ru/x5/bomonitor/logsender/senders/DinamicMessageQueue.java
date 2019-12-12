package ru.x5.bomonitor.logsender.senders;

/**
 * Псевдообъект для работы с отсылкой. Singletone
 */
public class DinamicMessageQueue {
    private SenderCache senderCache;

    /**
     * Возвращает один инстанс.
     */
    public DinamicMessageQueue() {
        senderCache = SenderCache.getInstance();
    }


    /**
     * Пытается отправить первую строку в очереди на отправку, если таковые есть.
     */
    public void sendFirst() {
        senderCache.sendFirst();
    }


}
