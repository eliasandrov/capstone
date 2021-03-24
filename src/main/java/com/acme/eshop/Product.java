package com.acme.eshop;

import java.lang.*;

public class Product {
    private int code;
    private String description ;
    private Float price;

    //constructors
    public Product() {
    }

    public Product(int code, String description, Float price) {
        this.code=code;
        this.description=description;
        this.price=price;
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
