package com.example.sellerapp1;

import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private List<String> orderIds;

    private OrderManager() {
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public List<String> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<String> orderIds) {
        this.orderIds = orderIds;
    }
}

