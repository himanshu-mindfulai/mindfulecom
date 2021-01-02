package com.developndesign.salonvendor.model.coupon;

import java.io.Serializable;

public class CouponDiscountModel implements Serializable {
    private String code;
    private double discountAmt;

    public String getCode() {
        return code;
    }

    public double getDiscountAmt() {
        return discountAmt;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountAmt(double discountAmt) {
        this.discountAmt = discountAmt;
    }
}
