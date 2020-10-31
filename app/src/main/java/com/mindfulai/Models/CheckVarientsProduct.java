package com.mindfulai.Models;

public class CheckVarientsProduct {
    private String available;
    private String id;
    private Integer stock;

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public CheckVarientsProduct() {
    }

    public CheckVarientsProduct(String available, String id, Integer stock) {
        this.available = available;
        this.id = id;
        this.stock = stock;
    }
}
