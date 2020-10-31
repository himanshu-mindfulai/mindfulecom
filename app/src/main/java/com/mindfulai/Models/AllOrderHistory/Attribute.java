
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute implements Parcelable {

    @SerializedName("attribute")
    @Expose
    private Attribute_ attribute;
    @SerializedName("option")
    @Expose
    private Option option;

    public Option getOption() {
        return option;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.attribute, flags);
        dest.writeParcelable(this.option, flags);
    }

    protected Attribute(Parcel in) {
        this.attribute = in.readParcelable(Attribute_.class.getClassLoader());
        this.option = in.readParcelable(Option.class.getClassLoader());
    }

    public static final Parcelable.Creator<Attribute> CREATOR = new Parcelable.Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel source) {
            return new Attribute(source);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };
}
