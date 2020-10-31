package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class UserBaseAddress implements Parcelable {
    private int status;
    private String message;
    private boolean errors;
    private ArrayList<UserDataAddress> data;

    public UserBaseAddress(int status, String message, boolean errors, ArrayList<UserDataAddress> data) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.data = data;
    }

    public UserBaseAddress() {
    }

    protected UserBaseAddress(Parcel in) {
        status = in.readInt();
        message = in.readString();
        errors = in.readByte() != 0;
    }

    public static final Creator<UserBaseAddress> CREATOR = new Creator<UserBaseAddress>() {
        @Override
        public UserBaseAddress createFromParcel(Parcel in) {
            return new UserBaseAddress(in);
        }

        @Override
        public UserBaseAddress[] newArray(int size) {
            return new UserBaseAddress[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }

    public ArrayList<UserDataAddress> getData() {
        return data;
    }

    public void setData(ArrayList<UserDataAddress> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(message);
        dest.writeByte((byte) (errors ? 1 : 0));
    }
}
