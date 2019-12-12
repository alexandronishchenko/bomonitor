package ru.x5.bomonitor.kafkaclient;


import org.apache.kafka.clients.producer.KafkaProducer;
import ru.x5.bomonitor.bomonitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class KafkaService<K,V> {
    Properties config;



    public KafkaProducer<K, V> getProducer() throws UnknownHostException {
        config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("bootstrap.servers",bomonitor.properties.getProperty("kafka.servers"));
        config.put("acks", "all");
        return new KafkaProducer<K, V>(config);
    }

}
