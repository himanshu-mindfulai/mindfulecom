
package com.mindfulai.Models.varientsByCategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Attribute_ implements Parcelable, Serializable {

    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;

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
        dest.writeValue(this.isActive);
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    public Attribute_() {
    }

    protected Attribute_(Parcel in) {
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.id = in.readString();
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
