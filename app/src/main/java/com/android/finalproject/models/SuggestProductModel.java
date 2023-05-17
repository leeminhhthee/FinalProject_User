package com.android.finalproject.models;

import java.io.Serializable;

public class SuggestProductModel implements Serializable {
    String id;
    String description;
    String name;
    int price;
    String img_url;
    String pro_brand;

    public SuggestProductModel() {
    }

    public SuggestProductModel(String id, String description, String name, int price, String img_url, String pro_brand) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.price = price;
        this.img_url = img_url;
        this.pro_brand = pro_brand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getPro_brand() {
        return pro_brand;
    }

    public void setPro_brand(String pro_brand) {
        this.pro_brand = pro_brand;
    }
}
