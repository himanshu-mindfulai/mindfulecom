
package com.mindfulai.Models.varientsByCategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Reviews implements Parcelable , Serializable {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("rating")
    @Expose
    private Double rating;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.total);
        dest.writeValue(this.rating);
    }

    public Reviews() {
    }

    protected Reviews(Parcel in) {
        this.total = (Integer) in.readValue(Double.class.getClassLoader());
        this.rating = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };
}
