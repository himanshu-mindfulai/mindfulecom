
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductReturnModel implements Parcelable {

    @SerializedName("product")
    @Expose
    private ProductReturnModel_ product;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("sellingPrice")
    @Expose
    private float sellingPrice;
    @SerializedName("discountAmt")
    @Expose
    private double discountAmount;
    @SerializedName("at")
    @Expose
    private String at;
    @SerializedName("status")
    @Expose
    private String status;

    public float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public ProductReturnModel_ getProduct() {
        return product;
    }

    public void setProduct(ProductReturnModel_ product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.product, flags);
        dest.writeValue(this.quantity);
        dest.writeFloat(this.sellingPrice);
        dest.writeDouble(this.discountAmount);
        dest.writeString(this.at);
        dest.writeString(this.status);
    }

    public ProductReturnModel() {
    }

    protected ProductReturnModel(Parcel in) {
        this.product = in.readParcelable(ProductReturnModel_.class.getClassLoader());
        this.quantity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sellingPrice = in.readFloat();
        this.discountAmount = in.readDouble();
        this.at = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<ProductReturnModel> CREATOR = new Parcelable.Creator<ProductReturnModel>() {
        @Override
        public ProductReturnModel createFromParcel(Parcel source) {
            return new ProductReturnModel(source);
        }

        @Override
        public ProductReturnModel[] newArray(int size) {
            return new ProductReturnModel[size];
        }
    };
}
