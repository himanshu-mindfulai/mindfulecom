package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VendorChild implements Parcelable {
    @SerializedName("mobile_number")
    @Expose
    private String mobile_number;

    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;


    @SerializedName("active")
    @Expose
    private boolean active;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("full_name")
    @Expose
    private String full_name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("role")
    @Expose
    private String role;

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    protected VendorChild(Parcel in) {
        mobile_number = in.readString();
        active = in.readByte() != 0;
        address = in.readString();
        _id = in.readString();
        full_name = in.readString();
        email = in.readString();
        profile_picture = in.readString();
        role = in.readString();
    }

    public static final Creator<VendorChild> CREATOR = new Creator<VendorChild>() {
        @Override
        public VendorChild createFromParcel(Parcel in) {
            return new VendorChild(in);
        }

        @Override
        public VendorChild[] newArray(int size) {
            return new VendorChild[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobile_number);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(address);
        dest.writeString(_id);
        dest.writeString(full_name);
        dest.writeString(email);
        dest.writeString(profile_picture);
        dest.writeString(role);
    }
}
