package ru.x5.bomonitor.Services.ZQL;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import ru.x5.bomonitor.Services.ServiceInterface;
import ru.x5.bomonitor.bomonitor;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class NativeServiceTest {
    /**
     * Проверка на корректную обработку длинны параметров 3,4,2,1,5.
     */
    @Test
    public void getMetric() {
        HashMap<String, ServiceInterface> services = bomonitor.initialize();
        //Mockito.mock()
    }
}