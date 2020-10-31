
package com.mindfulai.Models.BestSellingData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.varientsByCategory.Attribute__;
import com.mindfulai.Models.varientsByCategory.Product;
import com.mindfulai.Models.varientsByCategory.Varient;

import java.util.ArrayList;
import java.util.List;

public class BestSellingDataDatum implements Parcelable {

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

    public BestSellingDataDatum() {
    }

    protected BestSellingDataDatum(Parcel in) {
        this.varients = new ArrayList<Varient>();
        in.readList(this.varients, Varient.class.getClassLoader());
        this.attributes = in.createTypedArrayList(Attribute__.CREATOR);
        this.product = in.readParcelable(Product.class.getClassLoader());
    }

    public static final Creator<BestSellingDataDatum> CREATOR = new Creator<BestSellingDataDatum>() {
        @Override
        public BestSellingDataDatum createFromParcel(Parcel source) {
            return new BestSellingDataDatum(source);
        }

        @Override
        public BestSellingDataDatum[] newArray(int size) {
            return new BestSellingDataDatum[size];
        }
    };
}
