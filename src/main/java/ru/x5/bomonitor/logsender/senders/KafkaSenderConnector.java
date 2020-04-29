package ru.x5.bomonitor.logsender.senders;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import ru.x5.bomonitor.Logger.LogLevel;
import ru.x5.bomonitor.Logger.Logger;
import ru.x5.bomonitor.bomonitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Имплиментация коннектора для кафки. Включается из параметров.
 * Включается параметром log.consumers
 */
public class KafkaSenderConnector implements SenderConnector {
    Logger logger = bomonitor.getLogger();
    /**
     * Продюсер кафки
     */
    KafkaProducer<byte[], byte[]> producer;
    /**
     * Параметрый кафка-продюссера.
     */
    Properties config;
    /**
     * Ключ отправки. Устанавливается по хосту.
     */
    String key = null;

    /**
     * Конструктор. Использует дефолтный сериаллайзер кафки. Требует параметры в файле:
     * kafka.servers, также для класса необходимы при отправке:
     * kafka.topic
     */
    public KafkaSenderConnector() {
        try {
            config = new Properties();
            key = InetAddress.getLocalHost().getHostName();
            config.put("client.id", key);
            config.put("bootstrap.servers", bomonitor.properties.getProperty("kafka.servers"));
            config.put("acks", bomonitor.properties.getProperty("kafka.acks"));
            config.put("key.serializer", ByteArraySerializer.class);
            config.put("value.serializer", ByteArraySerializer.class);
        } catch (UnknownHostException e) {
            logger.insertRecord(this, "Host not found for key value.", LogLevel.error);
            //e.printStackTrace();
        }
        producer = new KafkaProducer<byte[], byte[]>(config);
    }

    /**
     * Непосредственная отправка в кафку.
     *
     * @param s Строка для отправки.
     * @return Успешно или нет.
     */
    @Override
    public boolean sendLine(String s) {
        Future<RecordMetadata> success = null;
        String key = null;
        try {
            key = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.insertRecord(this, "Host not found for key value.", LogLevel.error);
            //e.printStackTrace();
        }
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(bomonitor.properties.getProperty("kafka.topic"), key.getBytes(), s.getBytes());
        success = producer.send(record, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e != null)
                    System.out.println("Problem with sending: " + s);
                e.printStackTrace();
                logger.insertRecord(this, "Record was not successfully sended." + s, LogLevel.debug);
            }
        });
        return true;//success.isDone();
    }
}
