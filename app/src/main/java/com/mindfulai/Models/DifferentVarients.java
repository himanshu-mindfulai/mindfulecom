package com.mindfulai.Models;

import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

public class DifferentVarients implements Serializable {
    private String price;
    private String sellingPrice;
    private ArrayList<String> option_value;
    private String id;
    private String description;
    private String stock;
    private Integer minQty;
    private ArrayList<String> imagesList;
    private long inCart;

    public DifferentVarients(String price, ArrayList<String> option_value, String id, String description, String stock, String selling_price, Integer minQty,ArrayList<String> imagesList) {
        this.price = price;
        this.id = id;
        this.option_value = option_value;
        this.description = description;
        this.stock = stock;
        this.sellingPrice = selling_price;
        this.minQty = minQty;
        this.imagesList=imagesList;
        this.inCart = 0;
    }

    public DifferentVarients(String price, ArrayList<String> option_value, String id, String description, String stock, String selling_price, Integer minQty,ArrayList<String> imagesList, Long inCart) {
        this.price = price;
        this.id = id;
        this.option_value = option_value;
        this.description = description;
        this.stock = stock;
        this.sellingPrice = selling_price;
        this.minQty = minQty;
        this.imagesList=imagesList;
        this.inCart = inCart;
    }

    public long getInCart() {
        return inCart;
    }

    public void setInCart(long inCart) {
        this.inCart = inCart;
    }

    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public Integer getMinQty() {
        return minQty;
    }

    public void setMinQty(Integer minQty) {
        this.minQty = minQty;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DifferentVarients() {
    }

    private DifferentVarients(Parcel in) {
        price = in.readString();
        sellingPrice = in.readString();
        option_value = in.createStringArrayList();
        id = in.readString();
        description = in.readString();
        stock = in.readString();
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getOption_value() {
        return option_value;
    }

    public void setOption_value(ArrayList<String> option_value) {
        this.option_value = option_value;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}
