
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatedBy implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("role")
    @Expose
    private String role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeValue(this.active);
        dest.writeString(this.fullName);
        dest.writeString(this.email);
        dest.writeString(this.password);
        dest.writeString(this.role);
    }

    public CreatedBy() {
    }

    protected CreatedBy(Parcel in) {
        this.id = in.readString();
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.fullName = in.readString();
        this.email = in.readString();
        this.password = in.readString();
        this.role = in.readString();
    }

    public static final Parcelable.Creator<CreatedBy> CREATOR = new Parcelable.Creator<CreatedBy>() {
        @Override
        public CreatedBy createFromParcel(Parcel source) {
            return new CreatedBy(source);
        }

        @Override
        public CreatedBy[] newArray(int size) {
            return new CreatedBy[size];
        }
    };
}
