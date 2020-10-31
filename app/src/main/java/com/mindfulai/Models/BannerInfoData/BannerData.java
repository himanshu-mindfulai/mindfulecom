package com.mindfulai.Models.BannerInfoData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BannerData implements Parcelable {

@SerializedName("status")
@Expose
private Integer status;
@SerializedName("data")
@Expose
private List<BannerInfo> data = null;
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

public List<BannerInfo> getData() {
return data;
}

public void setData(List<BannerInfo> data) {
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

    public BannerData() {
    }

    protected BannerData(Parcel in) {
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.data = in.createTypedArrayList(BannerInfo.CREATOR);
        this.errors = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.message = in.readString();
    }

    public static final Creator<BannerData> CREATOR = new Creator<BannerData>() {
        @Override
        public BannerData createFromParcel(Parcel source) {
            return new BannerData(source);
        }

        @Override
        public BannerData[] newArray(int size) {
            return new BannerData[size];
        }
    };
}