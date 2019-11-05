package ru.x5.bomonitor.database.Entity;

import ru.x5.bomonitor.database.Table;

import java.util.ArrayList;

public class POS {
    String name;
    Table<ItemPrice> prices;
    public POS(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPrices(Table<ItemPrice> prices) {
        this.prices = prices;
    }
}
