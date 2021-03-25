package com.acme.eshop;

public class OrderService {

    public OrderService() {
    }

    public double getDiscount(Order order) {
        double discount=0.00;

        if (order.getPaymentType()== Order.PayentType.CASH){
            discount+=0.10;
        }else if (order.getPaymentType()== Order.PayentType.CREDIT){
            discount+=0.15;
        }

        return discount;
    }

    public Order createNewOrder(int orderID, Customer customer, Order.PayentType paymentType){
        return new Order(orderID, customer, paymentType);
    }

    public void addNewOrderItem(int orderitemID , Order order, Product product, int quantity){
        /* Individuals get no discount
        ● Business users get a 20% discount
        ● Government users get a 50% discount
        ● 10% discount when the customer pays by wire transfer
        ● 15% discount when the customer uses a credit card
         */

        double discount ;

        CustomerService cs = new CustomerService();
        OrderService os = new OrderService();

        //find discount
        discount = cs.getDiscount(order.getCustomer())+os.getDiscount(order);

        //add the item to the order
        order.addOrderItem(new OrderItem(orderitemID,product,quantity));

        //update the order total with the discount
        order.setDiscount(discount);

    }


}
