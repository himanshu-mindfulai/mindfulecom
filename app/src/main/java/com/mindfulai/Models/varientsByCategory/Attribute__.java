
package com.mindfulai.Models.varientsByCategory;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute__ implements Parcelable, Serializable {

    @SerializedName("attribute")
    @Expose
    private Attribute___ attribute;
    @SerializedName("option")
    @Expose
    private List<Option_> option = null;

    public Attribute___ getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute___ attribute) {
        this.attribute = attribute;
    }

    public List<Option_> getOption() {
        return option;
    }

    public void setOption(List<Option_> option) {
        this.option = option;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.attribute, flags);
        dest.writeList(this.option);
    }

    public Attribute__() {
    }

    protected Attribute__(Parcel in) {
        this.attribute = in.readParcelable(Attribute___.class.getClassLoader());
        this.option = new ArrayList<Option_>();
        in.readList(this.option, Option_.class.getClassLoader());
    }

    public static final Parcelable.Creator<Attribute__> CREATOR = new Parcelable.Creator<Attribute__>() {
        @Override
        public Attribute__ createFromParcel(Parcel source) {
            return new Attribute__(source);
        }

        @Override
        public Attribute__[] newArray(int size) {
            return new Attribute__[size];
        }
    };
}
