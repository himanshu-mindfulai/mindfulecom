
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product implements Parcelable {

    @SerializedName("product")
    @Expose
    private Product_ product;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("sellingPrice")
    @Expose
    private float sellingPrice;
    @SerializedName("discountAmt")
    @Expose
    private  double discountAmount;

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

    public Product_ getProduct() {
        return product;
    }

    public void setProduct(Product_ product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.product, flags);
        dest.writeValue(this.quantity);
        dest.writeValue(this.sellingPrice);
        dest.writeValue(this.discountAmount);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.product = in.readParcelable(Product_.class.getClassLoader());
        this.quantity = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sellingPrice = (float) in.readValue(Float.class.getClassLoader());
        this.discountAmount = (double) in.readValue(Double.class.getClassLoader());
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
