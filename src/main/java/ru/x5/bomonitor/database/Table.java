package ru.x5.bomonitor.database;

import java.util.ArrayList;

public class Table<E> {
    ArrayList<E> list= new ArrayList<>();


    public void put(E e){
        this.list.add(e);
    }
    public ArrayList<E> getList(){
        return this.list;
    }
}
