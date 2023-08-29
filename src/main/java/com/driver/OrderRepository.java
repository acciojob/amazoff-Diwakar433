package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

@Repository
public class OrderRepository {

    Map<String, Order> dbOrder = new HashMap<>();
    Map<String, DeliveryPartner> dbDeliveryPartner = new HashMap<>();
    Map<String, List<String>> dbPartnerOrder = new HashMap<>();
    Map<String, String> dbOrderPartner = new HashMap<>();
    public void addOrder(Order order) {
        dbOrder.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        dbDeliveryPartner.put(partnerId, partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {

        if(dbOrder.containsKey(orderId) && dbDeliveryPartner.containsKey(partnerId)) {
            dbOrderPartner.put(orderId, partnerId);

            List<String> orderList = new ArrayList<>();

            if(dbPartnerOrder.containsKey(partnerId)) {
                orderList = dbPartnerOrder.get(partnerId);
            }

            orderList.add(orderId);

            dbPartnerOrder.put(partnerId, orderList);

            dbDeliveryPartner.get(partnerId).setNumberOfOrders(orderList.size());
        }
    }

    public Order getOrderById(String orderId) {
        return dbOrder.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return dbDeliveryPartner.get(partnerId);
    }

    public static Integer getOrderCountByPartnerId(String partnerId) {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        return deliveryPartner.getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return dbPartnerOrder.get(partnerId);
    }


    public List<String> getAllOrders() {
        return new ArrayList<>(dbOrder.keySet());
    }

    public Integer getCountOfUnassignedOrders() {
        return getAllOrders().size()-dbOrderPartner.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId) {

        int countleftOrder = 0;

        List<String> orderList = dbPartnerOrder.get(partnerId);
        for(String order : orderList) {

            if(dbOrder.get(order).getDeliveryTime() > time) {
                countleftOrder++;
            }
        }
        return countleftOrder;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        List<String> orderList = dbPartnerOrder.get(partnerId);
        int max = Integer.MIN_VALUE;
        for (String order : orderList) {
            Order o = dbOrder.get(order);
            if(max < o.getDeliveryTime()) {
                max = o.getDeliveryTime();
            }
        }
        int h = max/60;
        int m = max%60;

        String hh = String.valueOf(h);
        String mm = String.valueOf(m);

        if(hh.length() < 2)
            hh = '0'+ hh;
        if(mm.length() < 2)
            mm = '0' + mm;

        return hh + ':' + mm;
    }

    public void deletePartnerById(String partnerId) {

        dbDeliveryPartner.remove(partnerId);

        List<String> orderList = dbPartnerOrder.get(partnerId);
        dbPartnerOrder.remove(partnerId);

        for(String order : orderList) {
            dbOrderPartner.remove(order);
        }
    }

    public void deleteOrderById(String orderId) {
        dbOrder.remove(orderId);
        String partnerId = dbOrderPartner.get(orderId);
        dbOrderPartner.remove(orderId);
        dbDeliveryPartner.get(partnerId).setNumberOfOrders(dbPartnerOrder.get(partnerId).size());
    }
}
