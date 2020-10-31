package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDataAddress implements Parcelable {
    private String _id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String user;
    private String name;
    private String mobile_number;



    public UserDataAddress() {
    }

    protected UserDataAddress(Parcel in) {
        _id = in.readString();
        addressLine1 = in.readString();
        addressLine2 = in.readString();
        city = in.readString();
        state = in.readString();
        pincode = in.readString();
        user = in.readString();
    }

    public static final Creator<UserDataAddress> CREATOR = new Creator<UserDataAddress>() {
        @Override
        public UserDataAddress createFromParcel(Parcel in) {
            return new UserDataAddress(in);
        }

        @Override
        public UserDataAddress[] newArray(int size) {
            return new UserDataAddress[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UserDataAddress(String _id, String addressLine1, String addressLine2, String city, String state, String pincode, String user, String name, String mobile_number) {
        this._id = _id;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.user = user;
        this.name = name;
        this.mobile_number = mobile_number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(addressLine1);
        dest.writeString(addressLine2);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(pincode);
        dest.writeString(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
}
