
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product__ implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("stock")
    @Expose
    private Integer stock;
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
    private CreatedBy createdBy;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

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

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.images, flags);
        dest.writeString(this.details);
        dest.writeValue(this.isActive);
        dest.writeValue(this.stock);
        dest.writeParcelable(this.brand, flags);
        dest.writeParcelable(this.category, flags);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeString(this.productId);
        dest.writeString(this.createdDate);
    }

    public Product__() {
    }

    protected Product__(Parcel in) {
        this.id = in.readString();
        this.images = in.readParcelable(Images.class.getClassLoader());
        this.details = in.readString();
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.stock = (Integer) in.readValue(Integer.class.getClassLoader());
        this.brand = in.readParcelable(Brand.class.getClassLoader());
        this.category = in.readParcelable(Category.class.getClassLoader());
        this.name = in.readString();
        this.type = in.readString();
        this.createdBy = in.readParcelable(CreatedBy.class.getClassLoader());
        this.productId = in.readString();
        this.createdDate = in.readString();
    }

    public static final Parcelable.Creator<Product__> CREATOR = new Parcelable.Creator<Product__>() {
        @Override
        public Product__ createFromParcel(Parcel source) {
            return new Product__(source);
        }

        @Override
        public Product__[] newArray(int size) {
            return new Product__[size];
        }
    };
}
