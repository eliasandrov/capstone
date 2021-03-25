package com.acme.eshop;

public class OrderItem {
    private int id;
    private Product product;
    private int quantity;
    private double totalCost ;

    public OrderItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public OrderItem(int id, Product product, int quantity) {
        this.id=id;
        this.product=product;
        this.quantity=quantity;
        this.totalCost=product.getPrice()*quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }
}
