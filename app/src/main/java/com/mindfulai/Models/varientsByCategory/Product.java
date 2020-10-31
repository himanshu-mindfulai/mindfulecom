
package com.mindfulai.Models.varientsByCategory;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Parcelable, Serializable {

    @SerializedName("details")
    @Expose
    private String details;

    @SerializedName("video")
    @Expose
    private String video;

    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("recommended")
    @Expose
    private Boolean isRecommended;
    @SerializedName("stock")
    @Expose
    private Integer stock;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("brand")
    @Expose
    private Brand brand;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("created_by")
    @Expose
    private Vendor createdBy;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("cod")
    @Expose
    private boolean codAvailable;

    @SerializedName("returnable")
    @Expose
    private boolean returnable;

    public boolean isReturnable() {
        return returnable;
    }

    public void setReturnable(boolean returnable) {
        this.returnable = returnable;
    }

    public String getVideo() {
        return video;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setIsRecommended(Boolean isRecommended) { this.isRecommended = isRecommended; }

    public Boolean getIsRecommended() { return isRecommended; }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vendor getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Vendor createdBy) {
        this.createdBy = createdBy;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public boolean isCodAvailable() {
        return codAvailable;
    }

    public void setCodAvailable(boolean codAvailable) {
        this.codAvailable = codAvailable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.details);
        dest.writeString(this.video);
        dest.writeValue(this.isActive);
        dest.writeValue(this.stock);
        dest.writeString(this.id);
        dest.writeParcelable(this.brand, flags);
        dest.writeParcelable(this.category, flags);
        dest.writeParcelable(this.images, flags);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeString(this.productId);
        dest.writeString(this.createdDate);
        dest.writeBoolean(this.codAvailable);
        dest.writeBoolean(this.returnable);
    }

    public Product() {
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Product(Parcel in) {
        this.details = in.readString();
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.stock = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = in.readString();
        this.brand = in.readParcelable(Brand.class.getClassLoader());
        this.category = in.readParcelable(Category.class.getClassLoader());
        this.images = in.readParcelable(Images.class.getClassLoader());
        this.name = in.readString();
        this.type = in.readString();
        this.createdBy = in.readParcelable(Vendor.class.getClassLoader());
        this.productId = in.readString();
        this.createdDate = in.readString();
        this.video = in.readString();
        this.codAvailable = in.readBoolean();
        this.returnable = in.readBoolean();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
