package com.example.sellerapp1;

import java.util.List;

public class Products {
    private int id; // This will be set by WooCommerce
    private String name;
    private String description;
    private String regular_price;
    private List<Image> images;
    private int vendor_id;

    // Constructor
    public Products(String name, String description, String regular_price, List<Image> images, int vendor_id) {
        this.name = name;
        this.description = description;
        this.regular_price = regular_price;
        this.images = images;
        this.vendor_id = vendor_id;
    }

    public Products(int id, String name, String description, String regular_price, List<Image> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.regular_price = regular_price;
        this.images = images;
    }



    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public List<Image> getImages() {
        return images;
    }


    public void setImages(List<Image> images) {
        this.images = images;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public static class Image {
        private int id;
        private String src;
        private String name;
        private String alt;


        public Image( String src, String name, String alt) {
            this.src = src;
            this.name = name;
            this.alt = alt;

        }

        public Image( String src) {
            this.src = src;
            this.name = name;
            this.alt = alt;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlt() {
            return alt;
        }

        public void setAlt(String alt) {
            this.alt = alt;
        }

    }
}
