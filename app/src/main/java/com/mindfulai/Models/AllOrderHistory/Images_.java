
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images_ implements Parcelable {

    @SerializedName("primary")
    @Expose
    private String primary;
    @SerializedName("secondary")
    @Expose
    private String secondary;

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getSecondary() {
        return secondary;
    }

    public void setSecondary(String secondary) {
        this.secondary = secondary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.primary);
        dest.writeString(this.secondary);
    }

    public Images_() {
    }

    protected Images_(Parcel in) {
        this.primary = in.readString();
        this.secondary = in.readString();
    }

    public static final Parcelable.Creator<Images_> CREATOR = new Parcelable.Creator<Images_>() {
        @Override
        public Images_ createFromParcel(Parcel source) {
            return new Images_(source);
        }

        @Override
        public Images_[] newArray(int size) {
            return new Images_[size];
        }
    };
}
