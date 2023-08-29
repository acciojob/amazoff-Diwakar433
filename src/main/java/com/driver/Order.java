package com.driver;

public class Order {

    private final String id;
    private final int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id = id;
        String[] time = deliveryTime.split(":");
        String hh = time[0];
        String mm = time[1];
        int h = Integer.parseInt(hh) * 60;
        int m = Integer.parseInt(mm);
        this.deliveryTime = h + m;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
