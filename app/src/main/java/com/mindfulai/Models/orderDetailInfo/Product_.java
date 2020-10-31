
package com.mindfulai.Models.orderDetailInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product_ implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("product")
    @Expose
    private Product__ product;
    @SerializedName("attributes")
    @Expose
    private List<Attribute> attributes = null;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private Float price;
    @SerializedName("stock")
    @Expose
    private Integer stock;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("sellingPrice")
    @Expose
    private float sellingPrice;

    public float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product__ getProduct() {
        return product;
    }

    public void setProduct(Product__ product) {
        this.product = product;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Product_() {
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeParcelable(this.product, flags);
        dest.writeTypedList(this.attributes);
        dest.writeString(this.description);
        dest.writeValue(this.price);
        dest.writeValue(this.stock);
        dest.writeParcelable(this.images, flags);
    }

    protected Product_(Parcel in) {
        this.id = in.readString();
        this.product = in.readParcelable(Product__.class.getClassLoader());
        this.attributes = in.createTypedArrayList(Attribute.CREATOR);
        this.description = in.readString();
        this.price = (Float) in.readValue(Integer.class.getClassLoader());
        this.stock = (Integer) in.readValue(Integer.class.getClassLoader());
        this.images = in.readParcelable(Images.class.getClassLoader());
    }

    public static final Creator<Product_> CREATOR = new Creator<Product_>() {
        @Override
        public Product_ createFromParcel(Parcel source) {
            return new Product_(source);
        }

        @Override
        public Product_[] newArray(int size) {
            return new Product_[size];
        }
    };
}
