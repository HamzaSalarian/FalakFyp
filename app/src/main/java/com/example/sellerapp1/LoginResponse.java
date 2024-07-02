package com.example.sellerapp1;

public class LoginResponse {
    private String status;
    private String message;
    private int user_id;
    private String consumer_key;
    private String consumer_secret;
    private String vendor_name;  // Add this line

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return user_id;
    }

    public String getConsumerKey() {
        return consumer_key;
    }

    public String getConsumerSecret() {
        return consumer_secret;
    }

    public String getVendorName() {  // Add this getter
        return vendor_name;
    }
}
