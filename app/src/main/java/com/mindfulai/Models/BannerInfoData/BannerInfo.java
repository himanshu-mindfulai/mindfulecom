package com.mindfulai.Models.BannerInfoData;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.Role;

public class BannerInfo implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("target")
    private Role target;

    @SerializedName("type")
    @Expose
    private String type;

    public Role getTarget() {
        return target;
    }

    public void setTarget(Role target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.image);
        dest.writeParcelable(this.target, flags);
        dest.writeString(this.type);
    }

    public BannerInfo() {
    }

    protected BannerInfo(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.target = in.readParcelable(Role.class.getClassLoader());
    }

    public static final Creator<BannerInfo> CREATOR = new Creator<BannerInfo>() {
        @Override
        public BannerInfo createFromParcel(Parcel source) {
            return new BannerInfo(source);
        }

        @Override
        public BannerInfo[] newArray(int size) {
            return new BannerInfo[size];
        }
    };
}