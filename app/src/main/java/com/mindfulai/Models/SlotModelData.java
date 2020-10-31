package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class SlotModelData implements Parcelable {
    private String _id;
    private String deleted;
    private String slot;

    public SlotModelData() {
    }

    protected SlotModelData(Parcel in) {
        _id = in.readString();
        deleted = in.readString();
        slot = in.readString();
    }

    public static final Creator<SlotModelData> CREATOR = new Creator<SlotModelData>() {
        @Override
        public SlotModelData createFromParcel(Parcel in) {
            return new SlotModelData(in);
        }

        @Override
        public SlotModelData[] newArray(int size) {
            return new SlotModelData[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public SlotModelData(String _id, String deleted, String slot) {
        this._id = _id;
        this.deleted = deleted;
        this.slot = slot;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(deleted);
        dest.writeString(slot);
    }
}
