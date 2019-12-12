package ru.x5.bomonitor.logparser.senders;

import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

/**
 * Главный поток отправки сообщений в консьюмеры.
 */
public class Sender implements Runnable {
    Logger logger = bomonitor.getLogger();
    /**
     * Параметр текущей работы. (мониторится для перезапуска).
     */
    private boolean running = false;
    /**
     * Управление очередью отправки.
     */
    private DinamicMessageQueue messageQueue;

    /**
     * Поток. Непосредственно отправляет и переотправляет очередь. Доступ осуществляется через DinamicMessageQueue
     */
    @Override
    public void run() {
        this.running = true;
        messageQueue = new DinamicMessageQueue();
        while (this.running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                logger.insertRecord(this, "Thread fails.", LogLevel.warn);
            }
            try {
                trySend();
            } catch (NullPointerException e) {
                System.out.println("retry");
                e.printStackTrace();
                logger.insertRecord(this, "send failed. Will retry. May nothing to send.", LogLevel.debug);
            }
        }
        running = false;
    }

    /**
     * Попытка отправить первую строку. DinamicMessageQueue
     */
    private void trySend() {
        messageQueue.sendFirst();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
