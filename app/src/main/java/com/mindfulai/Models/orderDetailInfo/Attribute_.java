
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute_ implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("name")
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeValue(this.isActive);
        dest.writeString(this.name);
    }

    public Attribute_() {
    }

    protected Attribute_(Parcel in) {
        this.id = in.readString();
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Attribute_> CREATOR = new Parcelable.Creator<Attribute_>() {
        @Override
        public Attribute_ createFromParcel(Parcel source) {
            return new Attribute_(source);
        }

        @Override
        public Attribute_[] newArray(int size) {
            return new Attribute_[size];
        }
    };
}
