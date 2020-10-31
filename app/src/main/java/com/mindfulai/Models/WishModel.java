package com.mindfulai.Models;

public class WishModel {
    String id;
    String details;
    String name;
    String image;
    int price;

    public WishModel(String id, String details, String name, String image, int price) {
        this.id = id;
        this.details = details;
        this.name = name;
        this.image = image;
        this.price = price;
    }
    WishModel(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
