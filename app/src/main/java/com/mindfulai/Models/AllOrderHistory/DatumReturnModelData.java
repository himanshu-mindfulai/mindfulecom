
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.UserDataAddress;

import java.util.ArrayList;
import java.util.List;

public class DatumReturnModelData implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("placed_by")
    @Expose
    private PlacedBy placedBy;

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @SerializedName("products")
    @Expose
    private List<Product> products;

    @SerializedName("amount")
    @Expose
    private Float amount;

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
    private Boolean isDelivered;

    @SerializedName("isCancelled")
    @Expose
    private Boolean isCancelled;
    @SerializedName("paymentMethod")
    @Expose
    private String paymentMethod;

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

    public PlacedBy getPlacedBy() {
        return placedBy;
    }

    public void setPlacedBy(PlacedBy placedBy) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isDelivered);
        dest.writeValue(this.isCancelled);
        dest.writeString(this.id);
        dest.writeParcelable(this.placedBy, flags);
        dest.writeString(this.orderId);
        dest.writeList(this.products);
        dest.writeValue(this.amount);
        dest.writeString(this.status);
        dest.writeString(this.orderDate);
        dest.writeString(this.deliverySlot);
        dest.writeParcelable(this.address, flags);
    }

    public DatumReturnModelData() {
    }

    protected DatumReturnModelData(Parcel in) {
        this.isDelivered = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isCancelled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.id = in.readString();
        this.placedBy = in.readParcelable(PlacedBy.class.getClassLoader());
        this.orderId = in.readString();
        this.products = new ArrayList<>();
        in.readList(this.products, Product.class.getClassLoader());
        this.address = in.readParcelable(UserDataAddress.class.getClassLoader());
        this.amount = (Float) in.readValue(Integer.class.getClassLoader());
        this.orderDate = in.readString();
        this.deliverySlot = in.readString();
    }

    public static final Parcelable.Creator<DatumReturnModelData> CREATOR = new Parcelable.Creator<DatumReturnModelData>() {
        @Override
        public DatumReturnModelData createFromParcel(Parcel source) {
            return new DatumReturnModelData(source);
        }

        @Override
        public DatumReturnModelData[] newArray(int size) {
            return new DatumReturnModelData[size];
        }
    };
}
