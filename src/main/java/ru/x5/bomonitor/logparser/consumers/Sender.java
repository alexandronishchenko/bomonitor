package ru.x5.bomonitor.logparser.consumers;

import ru.x5.bomonitor.bomonitor;

import java.util.Arrays;
import java.util.List;

public class Sender {

    private List<Consumer> senders;

    public Sender() {
        List<String> consNames= Arrays.asList(bomonitor.properties.getProperty("log.consumers").split(","));
        for(String s : consNames){
            switch (s){
                case "":
                    senders.add(new OutConsumer());
                    break;
                case "kafka":
                    senders.add(new KafkaConsumer());
                    break;
                case "out":
                    senders.add(new OutConsumer());
                    break;
            }
        }
    }

    public void sendLine(String line){
        senders.forEach(sender -> sendLine(line));
    }

}
