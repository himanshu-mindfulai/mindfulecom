package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product_info implements Parcelable {
    @SerializedName("is_active")
    @Expose
    private Boolean isActive;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("available_for")
    @Expose
    private List<AvailableFor> availableFor = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("brand")
    @Expose
    private Brand brand;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("category")
    @Expose
    private Category category;
    @SerializedName("farm_price")
    @Expose
    private Float farmPrice;
    @SerializedName("selling_price")
    @Expose
    private Float sellingPrice;
    @SerializedName("product_dms")
    @Expose
    private String productDms;
    @SerializedName("created_by")
    @Expose
    private CreatedBy createdBy;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("image")
    @Expose
    private String image;

//    Quantity added to cart
    private int quantity = 0;

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        quantity--;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<AvailableFor> getAvailableFor() {
        return availableFor;
    }

    public void setAvailableFor(List<AvailableFor> availableFor) {
        this.availableFor = availableFor;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Float getFarmPrice() {
        return farmPrice;
    }

    public void setFarmPrice(Float farmPrice) {
        this.farmPrice = farmPrice;
    }

    public Float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }


    public String getProductDms() {
        return productDms;
    }

    public void setProductDms(String productDms) {
        this.productDms = productDms;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isActive);
        dest.writeString(this.details);
        dest.writeTypedList(this.availableFor);
        dest.writeString(this.id);
        dest.writeParcelable(this.brand, flags);
        dest.writeString(this.name);
        dest.writeParcelable(this.category, flags);
        dest.writeValue(this.farmPrice);
        dest.writeValue(this.sellingPrice);
        dest.writeString(this.productDms);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeString(this.productId);
        dest.writeString(this.createdDate);
        dest.writeString(this.image);
        dest.writeInt(this.quantity);
    }

    public Product_info() {
    }

    protected Product_info(Parcel in) {
        this.isActive = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.details = in.readString();
        this.availableFor = in.createTypedArrayList(AvailableFor.CREATOR);
        this.id = in.readString();
        this.brand = in.readParcelable(Brand.class.getClassLoader());
        this.name = in.readString();
        this.category = in.readParcelable(Category.class.getClassLoader());
        this.farmPrice = (Float) in.readValue(Integer.class.getClassLoader());
        this.sellingPrice = (Float) in.readValue(Integer.class.getClassLoader());
        this.productDms = in.readString();
        this.createdBy = in.readParcelable(CreatedBy.class.getClassLoader());
        this.productId = in.readString();
        this.createdDate = in.readString();
        this.image = in.readString();
        this.quantity = in.readInt();
    }

    public static final Creator<Product_info> CREATOR = new Creator<Product_info>() {
        @Override
        public Product_info createFromParcel(Parcel source) {
            return new Product_info(source);
        }

        @Override
        public Product_info[] newArray(int size) {
            return new Product_info[size];
        }
    };
}