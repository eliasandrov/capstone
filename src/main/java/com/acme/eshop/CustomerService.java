package com.acme.eshop;

public class CustomerService {

    public CustomerService() {

    }

    public double getDiscount(Customer c) {
        double discount=0.00;

        if (c.getCustType() == Customer.CustType.B2C) {
            discount = 0.00;
        } else if (c.getCustType() == Customer.CustType.B2B) {
            discount = 0.20;
        } else if (c.getCustType() == Customer.CustType.B2G) {
            discount = 0.50;
        }

        return discount;

    }


}
