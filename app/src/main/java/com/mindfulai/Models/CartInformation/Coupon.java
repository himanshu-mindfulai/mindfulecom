package com.mindfulai.Models.CartInformation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coupon {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("discountAmt")
    @Expose
    private float discountAmt;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(float discountAmt) {
        this.discountAmt = discountAmt;
    }
}