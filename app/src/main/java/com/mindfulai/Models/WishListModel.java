
package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishListModel implements Parcelable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private List<ProductWishItem> data;
    @SerializedName("errors")
    @Expose
    private Boolean errors;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<ProductWishItem> getData() {
        return data;
    }

    public void setData(List<ProductWishItem> data) {
        this.data = data;
    }

    public Boolean getErrors() {
        return errors;
    }

    public void setErrors(Boolean errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.status);
        dest.writeTypedList(this.data);
        dest.writeValue(this.errors);
        dest.writeString(this.message);
    }

    public WishListModel() {
    }

    protected WishListModel(Parcel in) {
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.data = in.createTypedArrayList(ProductWishItem.CREATOR);
        this.errors = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
    }

    public static final Parcelable.Creator<WishListModel> CREATOR = new Parcelable.Creator<WishListModel>() {
        @Override
        public WishListModel createFromParcel(Parcel source) {
            return new WishListModel(source);
        }

        @Override
        public WishListModel[] newArray(int size) {
            return new WishListModel[size];
        }
    };
}
