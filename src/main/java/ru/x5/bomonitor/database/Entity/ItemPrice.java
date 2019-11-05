package ru.x5.bomonitor.database.Entity;

public class ItemPrice{
    private double price;
    private int priceType;
    private int itemId;

    public ItemPrice(int itemId,int priceType,double price) {
        this.price = price;
        this.priceType = priceType;
        this.itemId = itemId;
    }

    public double getPrice() {
        return price;
    }

    public int getPriceType() {
        return priceType;
    }

    public int getItemId() {
        return itemId;
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ItemPrice) {
            ItemPrice itemPrice=(ItemPrice) obj;
            return this.itemId == itemPrice.getItemId() && this.price ==itemPrice.getPrice() && this.priceType==itemPrice.getPriceType();
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}