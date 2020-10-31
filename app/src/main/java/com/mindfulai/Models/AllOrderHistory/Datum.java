
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.CartInformation.Coupon;
import com.mindfulai.Models.UserDataAddress;

import java.util.ArrayList;
import java.util.List;

public class Datum implements Parcelable {

    @SerializedName("deliveryCharge")
    @Expose
    private double deliveryCharge;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("placed_by")
    @Expose
    private String placedBy;

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @SerializedName("products")
    @Expose
    private List<Product> products;

    @SerializedName("amount")
    @Expose
    private float amount;

    @SerializedName("address")
    @Expose
    private UserDataAddress address;

    @SerializedName("deliverySlot")
    @Expose
    private String deliverySlot;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("order_date")
    @Expose
    private String orderDate;

    @SerializedName("isDelivered")
    @Expose
    private boolean isDelivered;

    @SerializedName("isCancelled")
    @Expose
    private boolean isCancelled;
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;
    @SerializedName("deliveryType")
    @Expose
    private String orderType;
    @SerializedName("paidFromWallet")
    @Expose
    private float paidFromWallet;

    @SerializedName("carryBagCharge")
    private float carryBagCharge;

    @SerializedName("coupon")
    private Coupon coupon;

    public float getCarryBagCharge() {
        return carryBagCharge;
    }

    public void setCarryBagCharge(float carryBagCharge) {
        this.carryBagCharge = carryBagCharge;
    }

    public float getPaidFromWallet() {
        return paidFromWallet;
    }

    public void setPaidFromWallet(float paidFromWallet) {
        this.paidFromWallet = paidFromWallet;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public UserDataAddress getAddress() {
        return address;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public void setAddress(UserDataAddress address) {
        this.address = address;
    }

    public Boolean getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlacedBy() {
        return placedBy;
    }

    public void setPlacedBy(String placedBy) {
        this.placedBy = placedBy;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isDelivered);
        dest.writeValue(this.isCancelled);
        dest.writeString(this.id);
        dest.writeString(this.placedBy);
        dest.writeString(this.orderId);
        dest.writeList(this.products);
        dest.writeValue(this.amount);
        dest.writeString(this.status);
        dest.writeString(this.orderDate);
        dest.writeString(this.deliverySlot);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.orderType);
        dest.writeFloat(this.paidFromWallet);
        dest.writeDouble(this.deliveryCharge);
        dest.writeValue(this.coupon);
    }

    public Datum() {
    }

    protected Datum(Parcel in) {
        this.isDelivered = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isCancelled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.id = in.readString();
        this.placedBy = in.readString();
        this.orderId = in.readString();
        this.products = new ArrayList<>();
        in.readList(this.products, Product.class.getClassLoader());
        this.address = in.readParcelable(UserDataAddress.class.getClassLoader());
        this.amount = (Float) in.readValue(Integer.class.getClassLoader());
        this.orderDate = in.readString();
        this.deliverySlot = in.readString();
        this.orderType = in.readString();
        this.paidFromWallet = in.readFloat();
        this.deliveryCharge = in.readDouble();
        this.coupon = (Coupon) in.readValue(Coupon.class.getClassLoader());
    }

    public static final Parcelable.Creator<Datum> CREATOR = new Parcelable.Creator<Datum>() {
        @Override
        public Datum createFromParcel(Parcel source) {
            return new Datum(source);
        }

        @Override
        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };
}
