package ru.x5.bomonitor.Services;
import java.lang.annotation.*;

@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface ServiceUnit {
    String value();
}
