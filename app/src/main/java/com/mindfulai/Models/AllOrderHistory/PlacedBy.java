
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlacedBy implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("full_name")
    @Expose
    private String fullName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.fullName);
    }

    public PlacedBy() {
    }

    protected PlacedBy(Parcel in) {
        this.id = in.readString();
        this.fullName = in.readString();
    }

    public static final Parcelable.Creator<PlacedBy> CREATOR = new Parcelable.Creator<PlacedBy>() {
        @Override
        public PlacedBy createFromParcel(Parcel source) {
            return new PlacedBy(source);
        }

        @Override
        public PlacedBy[] newArray(int size) {
            return new PlacedBy[size];
        }
    };
}
