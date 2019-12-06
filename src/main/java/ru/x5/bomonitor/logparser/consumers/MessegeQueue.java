package ru.x5.bomonitor.logparser.consumers;

import java.util.ArrayDeque;
import java.util.Deque;

public class MessegeQueue {
    Deque<String> messages;

    public MessegeQueue() {
        this.messages = new ArrayDeque<>();
    }

    public void putMessage(String message){
        this.messages.add(message);
    }

    public String getFirst(){
        return this.messages.getFirst();
    }
    public void deleteFirst(){
        this.messages.removeFirst();
    }
}
