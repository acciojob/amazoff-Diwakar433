package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

@Repository
public class OrderRepository {

    Map<String, Order> dbOrder = new HashMap<>();

    Map<String, DeliveryPartner> dbDeliveryPartner = new HashMap<>();
    Map<String, List<Order>> dbPartnerOrder = new HashMap<>();

    Map<String, String> dbOrderPartner = new HashMap<>();
    public void addOrder(Order order) {
        dbOrder.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        dbDeliveryPartner.put(partnerId, partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {

        Order order = dbOrder.get(orderId);
        DeliveryPartner partner = dbDeliveryPartner.get(partnerId);

        if(order == null || partner == null)
            return;

        if(dbPartnerOrder.containsKey(partnerId)) {
            dbPartnerOrder.get(partnerId).add(order);
        }
        else {
            List<Order> orderIdList = new ArrayList<>();
            orderIdList.add(order);
            dbPartnerOrder.put(partnerId, orderIdList);
        }
        dbOrderPartner.put(orderId, partnerId);
        partner.setNumberOfOrders(partner.getNumberOfOrders()+1);
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
        List<String> orderIdList = new ArrayList<>();
        List<Order> orderList = null;
        for(Map.Entry m : dbPartnerOrder.entrySet()) {
            if(dbPartnerOrder.containsKey(partnerId)) {
                orderList = dbPartnerOrder.get(partnerId);
                break;
            }
        }
        if(orderList == null)
            return orderIdList;
        for(Order order : orderList) {
            orderIdList.add(order.getId());
        }

        return orderIdList;
    }


    public List<String> getAllOrders() {
        List<String> allOrder = new ArrayList<>();
        for(String m : dbOrder.keySet()) {
            allOrder.add(m);
        }
        return allOrder;
    }

    public Integer getCountOfUnassignedOrders() {
        int allOrder = this.getAllOrders().size();

        int coutAssignOrder = 0;

        for(String m : dbPartnerOrder.keySet()) {
            coutAssignOrder += this.getOrderCountByPartnerId(m);
        }

        return allOrder-coutAssignOrder;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        String hh = time.substring(0,2);
        String mm = time.substring(3);
        int h = Integer.parseInt(hh) * 60;
        int m = Integer.parseInt(mm);
        int givenTime = h+m;


        int countleftOrder = 0;

        List<Order> orderList = null;
        for(Map.Entry el : dbPartnerOrder.entrySet()) {
            if(dbPartnerOrder.containsKey(partnerId)) {
                orderList = dbPartnerOrder.get(partnerId);
                break;
            }
        }
        if(orderList == null)
            return 0;
        for(Order order : orderList) {
            if(order.getDeliveryTime() > givenTime) {
                countleftOrder++;
            }
        }
        return countleftOrder;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        String time = "";

        List<Order> orderList = dbPartnerOrder.get(partnerId);
        int max = Integer.MIN_VALUE;
        for (Order order : orderList) {
            if(max < order.getDeliveryTime()) {
                max = order.getDeliveryTime();
            }
        }
        int h = max/60;
        int m = max%60;

        String hh = "";
        String mm = "";

        if (m >= 0 && m <=9) {
            mm = "0" + String.valueOf(m);
        }
        else {
            mm = String.valueOf(m);
        }

        if (h >= 0 && h <=9) {
            hh = "0" + String.valueOf(h);
        }
        else {
            hh = String.valueOf(h);
        }

        time = hh+mm;

        return time;
    }

    public void deletePartnerById(String partnerId) {
        DeliveryPartner d = new DeliveryPartner(partnerId);
        d.setNumberOfOrders(0);

        List<Order> list = dbPartnerOrder.get(partnerId);

        for(Order el : list) {
            dbOrderPartner.remove(el.getId());
        }
        dbPartnerOrder.remove(partnerId);
        dbDeliveryPartner.remove(partnerId);
    }

    public void deleteOrderById(String orderId) {
        String partnerId = dbOrderPartner.get(orderId);
        Order order = dbOrder.get(orderId);
        dbPartnerOrder.get(partnerId).remove(order);
        dbDeliveryPartner.get(partnerId).setNumberOfOrders(dbDeliveryPartner.get(partnerId).getNumberOfOrders()-1);
        dbOrderPartner.remove(orderId);
        dbOrder.remove(orderId);
    }
}
