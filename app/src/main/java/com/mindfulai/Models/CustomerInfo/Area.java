package com.mindfulai.Models.CustomerInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Area implements Parcelable {

@SerializedName("_id")
@Expose
private String id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("city")
@Expose
private String city;
@SerializedName("hub")
@Expose
private String hub;
@SerializedName("is_active")
@Expose
private Boolean isActive;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.city);
        dest.writeString(this.hub);
        dest.writeValue(this.isActive);
    }

    public Area() {
    }

    protected Area(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.city = in.readString();
        this.hub = in.readString();
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Area> CREATOR = new Parcelable.Creator<Area>() {
        @Override
        public Area createFromParcel(Parcel source) {
            return new Area(source);
        }

        @Override
        public Area[] newArray(int size) {
            return new Area[size];
        }
    };
}