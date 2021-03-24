package com.acme.eshop;

import java.util.ArrayList;

public class Order {
    public enum PayentType {CASH,CREDIT}

    private int orderId;
    private Customer customer;
    private ArrayList<OrderItem> orderItems = new ArrayList<>();//list of items in order
    private double totalAmountBeforeDiscount;
    private double discount;
    private double totalAmountAfterDiscount ;
    private PayentType paymentType;

    //constructor
    public Order(int orderID, Customer customer, PayentType paymentType) {
        this.orderId=orderID;
        this.customer=customer;
        this.paymentType=paymentType;
    }

    //adds an order item to the order
    public void addOrderItem(OrderItem oi){
        orderItems.add(oi);
        totalAmountBeforeDiscount+=oi.getTotalCost();
        totalAmountAfterDiscount=totalAmountBeforeDiscount*(1-discount);
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

    public double getTotalAmountBeforeDiscount() {
        return totalAmountBeforeDiscount;
    }

    public void setTotalAmountBeforeDiscount(double totalAmountBeforeDiscount) {
        this.totalAmountBeforeDiscount = totalAmountBeforeDiscount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        totalAmountAfterDiscount=totalAmountBeforeDiscount*(1-discount);
    }

    public double getTotalAmountAfterDiscount() {
        return totalAmountAfterDiscount;
    }

    public void setTotalAmountAfterDiscount(double totalAmountAfterDiscount) {
        this.totalAmountAfterDiscount = totalAmountAfterDiscount;
    }

    public PayentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PayentType paymentType) {
        this.paymentType = paymentType;
    }
}
