package ru.x5.bomonitor.JMXclient;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class JMXconnector {
    public static void main(String[] args) throws Exception {

        //Create an RMI connector client and connect it to the RMI connector server
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:6422/jmxrmi");
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();


        String domains[] = mbsc.getDomains();
        Arrays.sort(domains);
        for (String domain : domains) {
            System.out.println("\tDomain = " + domain);
        }

        System.out.println("\nMBeanServer default domain = " + mbsc.getDefaultDomain());

        System.out.println("\nMBean count = " +  mbsc.getMBeanCount());
        System.out.println("\nQuery MBeanServer MBeans:");
        Set<ObjectName> names =
                new TreeSet<ObjectName>(mbsc.queryNames(null, null));
        for (ObjectName name : names) {
            System.out.println("\tObjectName = " + name);
        }
    }
}
