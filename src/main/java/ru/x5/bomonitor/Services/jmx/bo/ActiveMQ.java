package ru.x5.bomonitor.Services.jmx.bo;

import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;

@ServiceUnitJMX("Сообщения в ActiveMQ")
public class ActiveMQ extends JMXparrent {

    public ActiveMQ() {
        this.name="activemq";
        this.value="org.apache.activemq:type=Broker,brokerName=brokerActiveMQ";
    }
}
