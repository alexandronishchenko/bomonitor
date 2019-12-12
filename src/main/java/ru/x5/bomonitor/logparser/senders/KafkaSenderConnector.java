package ru.x5.bomonitor.logparser.senders;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import ru.x5.bomonitor.bomonitor;
import ru.x5.bomonitor.kafkaclient.KafkaService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaSenderConnector implements SenderConnector {
    KafkaProducer<byte[],byte[]> producer;
//    KafkaService<String,String> kafkaService;
    Properties config;
    String key=null;

    public KafkaSenderConnector()  {
        try {
            config = new Properties();
            key=InetAddress.getLocalHost().getHostName();
            config.put("client.id", key);
            config.put("bootstrap.servers", bomonitor.properties.getProperty("kafka.servers"));
            config.put("acks", "all");
            config.put("key.serializer", ByteArraySerializer.class);
            config.put("value.serializer",ByteArraySerializer.class);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        producer=new KafkaProducer<byte[], byte[]>(config);
//        try {
//            producer=kafkaService.getProducer();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean sendLine(String s) {
        Future<RecordMetadata> success=null;
        String key= null;
        try {
            key = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(bomonitor.properties.getProperty("kafka.topic"), key.getBytes(), s.getBytes());
        success=producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null)
                System.out.println("Problem with sending: " + s);
                e.printStackTrace();}
        });
        return true;
    }
}
