package com.android.finalproject.models;

import java.util.List;

public class HistoryModel {
    String name;
    String phone;
    String email;
    String address;
    String date;
    int total;
    String status;
    List<HistoryProductModel> products;

    public HistoryModel() {
    }

    public HistoryModel(String name, String phone, String email, String address, String date, int total, String status, List<HistoryProductModel> products) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.date = date;
        this.total = total;
        this.status = status;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<HistoryProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<HistoryProductModel> products) {
        this.products = products;
    }
}