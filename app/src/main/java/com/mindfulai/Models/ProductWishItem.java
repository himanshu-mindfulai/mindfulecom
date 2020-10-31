package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductWishItem implements Parcelable {
    @SerializedName("product")
    @Expose
    private ProductWishList product;

    private ProductWishItem(Parcel in) {
        product = in.readParcelable(ProductWishList.class.getClassLoader());
    }

    public static final Creator<ProductWishItem> CREATOR = new Creator<ProductWishItem>() {
        @Override
        public ProductWishItem createFromParcel(Parcel in) {
            return new ProductWishItem(in);
        }

        @Override
        public ProductWishItem[] newArray(int size) {
            return new ProductWishItem[size];
        }
    };

    public ProductWishList getProduct() {
        return product;
    }

    public void setProduct(ProductWishList product) {
        this.product = product;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(product, flags);
    }
}
