package com.example.sellerapp1;

import java.util.List;

public class OrderResponse {
    private int order_count;
    private List<String> order_ids;

    public OrderResponse(int order_count, List<String> order_ids) {
        this.order_count = order_count;
        this.order_ids = order_ids;
    }

    public int getOrderCount() {
        return order_count;
    }

    public void setOrderCount(int order_count) {
        this.order_count = order_count;
    }

    public List<String> getOrderIds() {
        return order_ids;
    }

    public void setOrderIds(List<String> order_ids) {
        this.order_ids = order_ids;
    }
}
