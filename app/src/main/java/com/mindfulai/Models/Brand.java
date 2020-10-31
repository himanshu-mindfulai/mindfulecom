package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Brand implements Parcelable {

@SerializedName("_id")
@Expose
private String id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("logo")
@Expose
private String logo;
@SerializedName("contact")
@Expose
private String contact;
@SerializedName("address")
@Expose
private String address;

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

public String getLogo() {
return logo;
}

public void setLogo(String logo) {
this.logo = logo;
}

public String getContact() {
return contact;
}

public void setContact(String contact) {
this.contact = contact;
}

public String getAddress() {
return address;
}

public void setAddress(String address) {
this.address = address;
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.logo);
        dest.writeString(this.contact);
        dest.writeString(this.address);
    }

    public Brand() {
    }

    protected Brand(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.logo = in.readString();
        this.contact = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
        @Override
        public Brand createFromParcel(Parcel source) {
            return new Brand(source);
        }

        @Override
        public Brand[] newArray(int size) {
            return new Brand[size];
        }
    };
}