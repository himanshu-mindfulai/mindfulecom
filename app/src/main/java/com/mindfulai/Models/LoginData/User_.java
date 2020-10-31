package com.mindfulai.Models.LoginData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User_ implements Parcelable {

    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("street_address")
    @Expose
    private String streetAddress;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("anniversary")
    @Expose
    private String anniversary;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("profile_picture")
    @Expose
    private String profile_picture;
    @SerializedName("shop_name")
    @Expose
    private String shop_name;

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getRetailer_number() {
        return retailer_number;
    }

    public void setRetailer_number(String retailer_number) {
        this.retailer_number = retailer_number;
    }

    @SerializedName("retailer_number")
    @Expose
    private String retailer_number;

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public User_() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isActive);
        dest.writeString(this.id);
        dest.writeString(this.mobileNumber);
        dest.writeString(this.role);
        dest.writeString(this.fullName);
        dest.writeString(this.landmark);
        dest.writeString(this.streetAddress);
        dest.writeString(this.area);
        dest.writeString(this.dob);
        dest.writeString(this.anniversary);
        dest.writeString(this.userId);
        dest.writeString(this.profile_picture);
        dest.writeString(this.shop_name);
        dest.writeString(this.retailer_number);
    }

    protected User_(Parcel in) {
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.id = in.readString();
        this.mobileNumber = in.readString();
        this.role = in.readString();
        this.fullName = in.readString();
        this.landmark = in.readString();
        this.streetAddress = in.readString();
        this.area = in.readString();
        this.dob = in.readString();
        this.anniversary = in.readString();
        this.userId = in.readString();
        this.profile_picture = in.readString();
        this.shop_name = in.readString();
        this.retailer_number = in.readString();
    }

    public static final Creator<User_> CREATOR = new Creator<User_>() {
        @Override
        public User_ createFromParcel(Parcel source) {
            return new User_(source);
        }

        @Override
        public User_[] newArray(int size) {
            return new User_[size];
        }
    };
}