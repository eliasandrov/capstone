package com.acme.eshop;

import java.lang.*;

public class Product {
    private int code;
    private String description ;
    private double price;

    //constructors
    public Product() {
    }

    public Product(int code, String description, double price) {
        this.code=code;
        this.description=description;
        this.price=price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "code=" + code +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
