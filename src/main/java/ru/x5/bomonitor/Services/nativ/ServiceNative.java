package ru.x5.bomonitor.Services.nativ;
import java.lang.annotation.*;

@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ServiceNative {
    String value();
}
