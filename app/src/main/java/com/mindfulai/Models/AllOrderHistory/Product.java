
package com.mindfulai.Models.AllOrderHistory;

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
    private Float sellingPrice;
    @SerializedName("discountAmt")
    @Expose
    private double discountAmount;
    @SerializedName("at")
    @Expose
    private String at;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("returnable")
    @Expose
    private boolean returnable = false;

    protected Product(Parcel in) {
        product = in.readParcelable(Product_.class.getClassLoader());
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
        if (in.readByte() == 0) {
            sellingPrice = null;
        } else {
            sellingPrice = in.readFloat();
        }
        discountAmount = in.readDouble();
        at = in.readString();
        status = in.readString();
        returnable = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public boolean getReturnable() {
        return returnable;
    }

    public void setReturnable(boolean returnable) {
        this.returnable = returnable;
    }

    public Float getSellingPrice() {
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(product, i);
        if (quantity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(quantity);
        }
        if (sellingPrice == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(sellingPrice);
        }
        parcel.writeDouble(discountAmount);
        parcel.writeString(at);
        parcel.writeString(status);
        parcel.writeByte((byte) (returnable ? 1 : 0));
    }
}
