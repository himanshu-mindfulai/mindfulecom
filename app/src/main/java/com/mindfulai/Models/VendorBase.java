package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VendorBase  implements Parcelable {
    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("data")
    @Expose
    private List<VendorChild> data;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("errors")
    @Expose
    private boolean errors;

    protected VendorBase(Parcel in) {
        status = in.readInt();
        data = in.readParcelable(VendorChild.class.getClassLoader());
        message = in.readString();
        errors = in.readByte() != 0;
    }

    public static final Creator<VendorBase> CREATOR = new Creator<VendorBase>() {
        @Override
        public VendorBase createFromParcel(Parcel in) {
            return new VendorBase(in);
        }

        @Override
        public VendorBase[] newArray(int size) {
            return new VendorBase[size];
        }
    };

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<VendorChild> getData() {
        return data;
    }

    public void setData(List<VendorChild> data) {
        this.data = data;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeList(data);
        dest.writeString(message);
        dest.writeByte((byte) (errors ? 1 : 0));
    }
}
