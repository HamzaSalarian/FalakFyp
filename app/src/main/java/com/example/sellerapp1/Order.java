package com.example.sellerapp1;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("id")
    private String id;
    @SerializedName("billing")
    private Billing billing;
    @SerializedName("total")
    private String total;
    @SerializedName("status")
    private String status;

    public Order(String id, Billing billing, String total, String status) {
        this.id = id;
        this.billing = billing;
        this.total = total;
        this.status = status;
    }

    public Order(String status){
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Billing getBilling() {
        return billing;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Billing {
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;

        public Billing(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
