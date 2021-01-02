package com.developndesign.salonvendor.model.coupon;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CouponBaseModel {
    @SerializedName("status")
    private int status;

    @SerializedName("data")
    private ArrayList<CouponDataModel> data;

    @SerializedName("message")
    private String message;

    @SerializedName("errors")
    private boolean errors;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<CouponDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<CouponDataModel> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }
}
