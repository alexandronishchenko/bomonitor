package ru.x5.bomonitor.Services.logmonitoring;

public class ERROR {
    private String value;
    public errorLevels level;

    public ERROR(errorLevels level,String value) {
        this.level = level;
        this.value=value;
    }

    public errorLevels getLevel() {
        return level;
    }

    public void setLevel(errorLevels level) {
        this.level = level;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
