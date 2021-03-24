package com.acme.eshop;

public class OrderItem {
    private Product product;
    private int quantity;
    private Float totalCost ;

    public OrderItem() {
    }

    public OrderItem(Product product,int quantity) {
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

    public Float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }
}
