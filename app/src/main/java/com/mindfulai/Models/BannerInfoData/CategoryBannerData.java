package com.mindfulai.Models.BannerInfoData;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryBannerData implements Parcelable {
    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("image")
    @Expose
    private String image;

    protected CategoryBannerData(Parcel in) {
        category = in.readString();
        image = in.readString();
    }

    public static final Creator<CategoryBannerData> CREATOR = new Creator<CategoryBannerData>() {
        @Override
        public CategoryBannerData createFromParcel(Parcel in) {
            return new CategoryBannerData(in);
        }

        @Override
        public CategoryBannerData[] newArray(int size) {
            return new CategoryBannerData[size];
        }
    };

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category);
        parcel.writeString(image);
    }
}
