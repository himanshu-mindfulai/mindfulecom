package com.mindfulai.Models.CustomerInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

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
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("anniversary")
    @Expose
    private String anniversary;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    @SerializedName("landmark")
    @Expose
    private String landmark;
    @SerializedName("H_no_society")
    @Expose
    private String hNoSociety;
    @SerializedName("address")
    @Expose
    private String streetAddress;
    @SerializedName("area")
    @Expose
    private Area area;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("retailer_number")
    @Expose
    private String retailerNumber;
    @SerializedName("shop_name")
    @Expose
    private String shopName;
    @SerializedName("wallet")
    private float wallet;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("referralCode")
    @Expose
    private String referralCode;

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRetailerNumber() {
        return retailerNumber;
    }

    public void setRetailerNumber(String retailerNumber) {
        this.retailerNumber = retailerNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getHNoSociety() {
        return hNoSociety;
    }

    public void setHNoSociety(String hNoSociety) {
        this.hNoSociety = hNoSociety;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    public User() {
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
        dest.writeString(this.dob);
        dest.writeString(this.anniversary);
        dest.writeString(this.fullName);
        dest.writeString(this.profilePicture);
        dest.writeString(this.landmark);
        dest.writeString(this.hNoSociety);
        dest.writeString(this.streetAddress);
        dest.writeParcelable(this.area, flags);
        dest.writeString(this.userId);
        dest.writeString(this.retailerNumber);
        dest.writeString(this.shopName);
        dest.writeFloat(this.wallet);
        dest.writeString(this.email);
        dest.writeString(this.referralCode);
    }

    protected User(Parcel in) {
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.id = in.readString();
        this.mobileNumber = in.readString();
        this.role = in.readString();
        this.dob = in.readString();
        this.anniversary = in.readString();
        this.fullName = in.readString();
        this.profilePicture = in.readString();
        this.landmark = in.readString();
        this.hNoSociety = in.readString();
        this.streetAddress = in.readString();
        this.area = in.readParcelable(Area.class.getClassLoader());
        this.userId = in.readString();
        this.retailerNumber = in.readString();
        this.shopName = in.readString();
        this.wallet = in.readFloat();
        this.email = in.readString();
        this.referralCode = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}