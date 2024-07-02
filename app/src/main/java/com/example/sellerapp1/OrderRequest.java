package com.example.sellerapp1;

import java.util.List;

public class OrderRequest {
    private List<String> order_ids;
    private String consumer_key;
    private String consumer_secret;
    private String vendor_name;

    public OrderRequest(List<String> order_ids, String consumer_key, String consumer_secret) {
        this.order_ids = order_ids;
        this.consumer_key = consumer_key;
        this.consumer_secret = consumer_secret;
    }

    public OrderRequest(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public List<String> getOrder_ids() {
        return order_ids;
    }

    public void setOrder_ids(List<String> order_ids) {
        this.order_ids = order_ids;
    }

    public String getConsumer_key() {
        return consumer_key;
    }

    public void setConsumer_key(String consumer_key) {
        this.consumer_key = consumer_key;
    }

    public String getConsumer_secret() {
        return consumer_secret;
    }

    public void setConsumer_secret(String consumer_secret) {
        this.consumer_secret = consumer_secret;
    }
}
