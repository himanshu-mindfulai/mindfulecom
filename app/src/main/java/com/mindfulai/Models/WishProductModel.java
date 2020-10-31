package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WishProductModel implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String id;

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
    private String brand;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("created_by")
    @Expose
    private String createdBy;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
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

    public static Creator<WishProductModel> getCREATOR() {
        return CREATOR;
    }

    private WishProductModel(Parcel in) {
        id = in.readString();
        details = in.readString();
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        if (in.readByte() == 0) {
            stock = null;
        } else {
            stock = in.readInt();
        }
        brand = in.readString();
        category = in.readString();
        name = in.readString();
        type = in.readString();
        createdBy = in.readString();
        productId = in.readString();
        createdDate = in.readString();
    }

    public static final Creator<WishProductModel> CREATOR = new Creator<WishProductModel>() {
        @Override
        public WishProductModel createFromParcel(Parcel in) {
            return new WishProductModel(in);
        }

        @Override
        public WishProductModel[] newArray(int size) {
            return new WishProductModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(details);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        if (stock == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(stock);
        }
        dest.writeString(brand);
        dest.writeString(category);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(createdBy);
        dest.writeString(productId);
        dest.writeString(createdDate);
    }
}
