package ru.x5.bomonitor.Services.ZQL;

import org.junit.Test;
import ru.x5.bomonitor.Services.nativ.ServiceNativeInterface;
import ru.x5.bomonitor.bomonitor;

import java.util.HashMap;

public class NativeServiceTest {
    /**
     * Проверка на корректную обработку длинны параметров 3,4,2,1,5.
     */
    @Test
    public void getMetric() {
        HashMap<String, ServiceNativeInterface> services = bomonitor.initializeNativeServices();
        //Mockito.mock()
    }
}