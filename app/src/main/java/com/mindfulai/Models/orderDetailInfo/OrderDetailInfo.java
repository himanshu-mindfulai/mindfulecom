
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetailInfo implements Parcelable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("data")
    @Expose
    private Data data;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    public OrderDetailInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.status);
        dest.writeParcelable(this.data, flags);
        dest.writeValue(this.errors);
        dest.writeString(this.message);
    }

    protected OrderDetailInfo(Parcel in) {
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.data = in.readParcelable(Data.class.getClassLoader());
        this.errors = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
    }

    public static final Creator<OrderDetailInfo> CREATOR = new Creator<OrderDetailInfo>() {
        @Override
        public OrderDetailInfo createFromParcel(Parcel source) {
            return new OrderDetailInfo(source);
        }

        @Override
        public OrderDetailInfo[] newArray(int size) {
            return new OrderDetailInfo[size];
        }
    };
}
