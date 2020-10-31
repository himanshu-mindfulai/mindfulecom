
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.UserDataAddress;

import java.util.ArrayList;
import java.util.List;

public class Data implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("address")
    @Expose
    private UserDataAddress address;

    @SerializedName("isDelivered")
    @Expose
    private Boolean isDelivered;

    @SerializedName("isCancelled")
    @Expose
    private Boolean isCancelled;

    @SerializedName("placed_by")
    @Expose
    private String placedBy;

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @SerializedName("created_by")
    @Expose
    private String createdBy;

    @SerializedName("products")
    @Expose
    private List<Product> products = null;

    @SerializedName("amount")
    @Expose
    private Float amount;

    @SerializedName("order_date")
    @Expose
    private String orderDate;

    @SerializedName("deliverySlot")
    @Expose
    private String deliverySlot;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("order")
    @Expose
    private Data order;

    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;

    @SerializedName("deliveryType")
    @Expose
    private String orderType;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Data getOrder() {
        return order;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrder(Data order) {
        this.order = order;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public void setDeliverySlot(String deliverySlot) {
        this.deliverySlot = deliverySlot;
    }

    public UserDataAddress getAddress() {
        return address;
    }

    public void setAddress(UserDataAddress address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeValue(this.isDelivered);
        dest.writeValue(this.isCancelled);
        dest.writeString(this.placedBy);
        dest.writeString(this.orderId);
        dest.writeList(this.products);
        dest.writeValue(this.amount);
        dest.writeString(this.orderDate);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.deliverySlot);
        dest.writeString(this.action);
        dest.writeParcelable(this.order, flags);
        dest.writeString(this.createdBy);
        dest.writeString(this.paymentMethod);
        dest.writeString(this.orderType);
    }

    public Data() {
    }

    protected Data(Parcel in) {
        this.id = in.readString();
        this.isDelivered = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isCancelled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.placedBy = in.readString();
        this.orderId = in.readString();
        this.products = new ArrayList<Product>();
        in.readList(this.products, Product.class.getClassLoader());
        this.amount = (Float) in.readValue(Integer.class.getClassLoader());
        this.orderDate = in.readString();
        this.deliverySlot=in.readString();
        this.address = in.readParcelable(UserDataAddress.class.getClassLoader());
        this.order = in.readParcelable(Data.class.getClassLoader());
        this.createdBy = in.readString();
        this.paymentMethod = in.readString();
        this.orderType = in.readString();
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel source) {
            return new Data(source);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
