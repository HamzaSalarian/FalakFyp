package com.example.sellerapp1;

public class Order {
    private String orderId;
    private String customerName;
    private String totalPrice;

    public Order(String orderId, String customerName, String totalPrice) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }
}
