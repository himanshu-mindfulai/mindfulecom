package com.mindfulai.Models.WalletRechargeModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.SubcategoryModel.Datum;

import java.util.List;

public class WalletRechargeData {
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
