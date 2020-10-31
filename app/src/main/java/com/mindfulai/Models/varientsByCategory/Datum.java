
package com.mindfulai.Models.varientsByCategory;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum implements Parcelable,Serializable {

    @SerializedName("varients")
    @Expose
    private List<Varient> varients = null;
    @SerializedName("attributes")
    @Expose
    private List<Attribute__> attributes = null;
    @SerializedName("product")
    @Expose
    private Product product;

    public List<Varient> getVarients() {
        return varients;
    }

    public void setVarients(List<Varient> varients) {
        this.varients = varients;
    }

    public List<Attribute__> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute__> attributes) {
        this.attributes = attributes;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.varients);
        dest.writeTypedList(this.attributes);
        dest.writeParcelable(this.product, flags);
    }

    public Datum() {
    }

    protected Datum(Parcel in) {
        this.varients = new ArrayList<Varient>();
        in.readList(this.varients, Varient.class.getClassLoader());
        this.attributes = in.createTypedArrayList(Attribute__.CREATOR);
        this.product = in.readParcelable(Product.class.getClassLoader());
    }

    public static final Parcelable.Creator<Datum> CREATOR = new Parcelable.Creator<Datum>() {
        @Override
        public Datum createFromParcel(Parcel source) {
            return new Datum(source);
        }

        @Override
        public Datum[] newArray(int size) {
            return new Datum[size];
        }
    };
}
