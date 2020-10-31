package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SlotModelBase implements Parcelable {
    private int status;
    private String message;
    private boolean errors;
    private ArrayList<SlotModelData> data;

    public SlotModelBase() {
    }

    protected SlotModelBase(Parcel in) {
        status = in.readInt();
        message = in.readString();
        errors = in.readByte() != 0;
        data = in.createTypedArrayList(SlotModelData.CREATOR);
    }

    public static final Creator<SlotModelBase> CREATOR = new Creator<SlotModelBase>() {
        @Override
        public SlotModelBase createFromParcel(Parcel in) {
            return new SlotModelBase(in);
        }

        @Override
        public SlotModelBase[] newArray(int size) {
            return new SlotModelBase[size];
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

    public ArrayList<SlotModelData> getData() {
        return data;
    }

    public void setData(ArrayList<SlotModelData> data) {
        this.data = data;
    }

    public SlotModelBase(int status, String message, boolean errors, ArrayList<SlotModelData> data) {
        this.status = status;
        this.message = message;
        this.errors = errors;
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
        dest.writeTypedList(data);
    }
}
