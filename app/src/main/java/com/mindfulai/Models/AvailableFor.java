package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailableFor implements Parcelable {

@SerializedName("is_active")
@Expose
private Boolean isActive;
@SerializedName("_id")
@Expose
private String id;
@SerializedName("full_name")
@Expose
private String fullName;
@SerializedName("email")
@Expose
private String email;
@SerializedName("role")
@Expose
private String role;
@SerializedName("user_id")
@Expose
private String userId;
@SerializedName("mobile_number")
@Expose
private String mobileNumber;

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

public String getFullName() {
return fullName;
}

public void setFullName(String fullName) {
this.fullName = fullName;
}

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public String getRole() {
return role;
}

public void setRole(String role) {
this.role = role;
}

public String getUserId() {
return userId;
}

public void setUserId(String userId) {
this.userId = userId;
}

public String getMobileNumber() {
return mobileNumber;
}

public void setMobileNumber(String mobileNumber) {
this.mobileNumber = mobileNumber;
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isActive);
        dest.writeString(this.id);
        dest.writeString(this.fullName);
        dest.writeString(this.email);
        dest.writeString(this.role);
        dest.writeString(this.userId);
        dest.writeString(this.mobileNumber);
    }

    public AvailableFor() {
    }

    protected AvailableFor(Parcel in) {
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.id = in.readString();
        this.fullName = in.readString();
        this.email = in.readString();
        this.role = in.readString();
        this.userId = in.readString();
        this.mobileNumber = in.readString();
    }

    public static final Parcelable.Creator<AvailableFor> CREATOR = new Parcelable.Creator<AvailableFor>() {
        @Override
        public AvailableFor createFromParcel(Parcel source) {
            return new AvailableFor(source);
        }

        @Override
        public AvailableFor[] newArray(int size) {
            return new AvailableFor[size];
        }
    };
}