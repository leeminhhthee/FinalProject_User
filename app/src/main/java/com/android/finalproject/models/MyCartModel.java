package com.android.finalproject.models;

public class MyCartModel {
    String productId;
    String currentTime;
    String currentDate;
    String productName;
    String productImg;
    int productPrice;
    int totalQty;
    int totalPrice;

    public MyCartModel() {
    }

    public MyCartModel(String productId, String currentTime, String currentDate, String productName, String productImg, int productPrice, int totalQty, int totalPrice) {
        this.productId = productId;
        this.currentTime = currentTime;
        this.currentDate = currentDate;
        this.productName = productName;
        this.productImg = productImg;
        this.productPrice = productPrice;
        this.totalQty = totalQty;
        this.totalPrice = totalPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
