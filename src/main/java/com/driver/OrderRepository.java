package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String, Order> orderHashMap = new HashMap<>();
    HashMap<String, DeliveryPartner> deliveryPartnerHashMap = new HashMap<>();

    HashMap<String, List<Order>> partnerOrderHashMap = new HashMap<>();

    HashMap<String, String> orderToPartner = new HashMap<>();


    public void addOrder(Order order) {
        orderHashMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner dp = new DeliveryPartner(partnerId);
        deliveryPartnerHashMap.put(partnerId,dp);
    }


    public void addOrderPartnerPair(String orderId, String partnerId) {
        Order order = orderHashMap.get(orderId);
        DeliveryPartner deliveryPartner = deliveryPartnerHashMap.get(partnerId);

        if (order == null || deliveryPartner == null) return;

        if (partnerOrderHashMap.containsKey(deliveryPartner.getId())) {
            partnerOrderHashMap.get(deliveryPartner.getId()).add(order);
        }

        else {
            List<Order> list = new ArrayList<>();
            list.add(order);
            partnerOrderHashMap.put(deliveryPartner.getId(), list);
        }

        orderToPartner.put(orderId,partnerId);

        deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders()+1);
    }


    public Order getOrderById(String orderId) {
        return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerHashMap.get(partnerId);
    }

    public static Integer getOrderCountByPartnerId(String partnerId) {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        return deliveryPartner.getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {

        List<Order> list = partnerOrderHashMap.get(partnerId);
        List<String> orders = new ArrayList<>();
        for (Order o: list) {
            orders.add(o.getId());
        }
        return orders;
    }

    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        for (String s: orderHashMap.keySet()) {
            orders.add(s);
        }

        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        Integer totalOrders = getAllOrders().size();

        Integer countOfAssignedOrders = 0;
        int ans = 0;

        for (String dp: partnerOrderHashMap.keySet()) {
            countOfAssignedOrders += getOrderCountByPartnerId(dp);
        }

        ans = totalOrders - countOfAssignedOrders;
        return ans;
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId) {

        List<Order> list = partnerOrderHashMap.get(partnerId);
        Integer count = 0;

        for (Order o: list) {
            if (o.getDeliveryTime() > time) {
                count++;
            }
        }

        return count;

    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {

        int time = 0;

        List<Order> list = partnerOrderHashMap.get(partnerId);

        for (Order o: list) {
            if (o.getDeliveryTime() > time) {
                time = o.getDeliveryTime();
            }
        }

        int h = time/60;
        int m = time%60;

        String hh = String.valueOf(h);
        String mm = String.valueOf(m);

        if(hh.length() < 2)
            hh = '0' + hh;

        if(mm.length() < 2)
            mm = '0' + mm;

        return hh + ':' + mm;
    }

    public void deletePartnerById(String partnerId) {
        DeliveryPartner dp = deliveryPartnerHashMap.get(partnerId);
        dp.setNumberOfOrders(0);

        List<Order> list = partnerOrderHashMap.get(partnerId);
        for (Order o: list) {
            orderToPartner.remove(o.getId());
        }
        deliveryPartnerHashMap.remove(partnerId);
        partnerOrderHashMap.remove(partnerId);

    }

    public void deleteOrderById(String orderId) {
        String partnerId = orderToPartner.get(orderId);
        Order order = orderHashMap.get(orderId);
        partnerOrderHashMap.get(partnerId).remove(order);
        deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(deliveryPartnerHashMap.get(partnerId).getNumberOfOrders()-1);
        orderToPartner.remove(orderId);
        orderHashMap.remove(orderId);
    }
}