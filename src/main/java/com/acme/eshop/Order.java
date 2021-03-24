package com.acme.eshop;

import java.util.ArrayList;

public class Order {
    public enum PayentType {CASH,CREDIT}

    private int orderId;
    private Customer customer;
    private ArrayList<OrderItem> orderItems = new ArrayList<>();//list of items in order
    private Float totalAmount ;//total order cost

    //constructor
    public Order() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }
}
