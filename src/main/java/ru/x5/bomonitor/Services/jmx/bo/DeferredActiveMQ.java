package ru.x5.bomonitor.Services.jmx.bo;


import ru.x5.bomonitor.Services.ZabbixRequest;
import ru.x5.bomonitor.Services.jmx.ServiceUnitJMX;
@ZabbixRequest("jmx.bo.defactivemq.QueueSize")
@ServiceUnitJMX("Отложенные сообщения MQ")
public class DeferredActiveMQ extends JMXparrent  {

    public DeferredActiveMQ() {
        this.name="defactivemq";
        this.value="org.apache.activemq:type=Broker,brokerName=brokerActiveMQ,destinationType=Queue,destinationName=sappi.deferred.export";
    }
}
