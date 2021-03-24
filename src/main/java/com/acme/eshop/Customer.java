package com.acme.eshop;

public class Customer {
    public enum CustType {B2B,B2C,B2G}

    private int code;
    private String name ;
    private CustType custType;
    
    //constructors
    public Customer(int code, String name,CustType type) {
        this.code=code;
        this.name=name;
        this.custType=type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustType getCustType() {
        return custType;
    }

    public void setCustType(CustType custType) {
        this.custType = custType;
    }
}
