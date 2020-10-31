
package com.mindfulai.Models.varientsByCategory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Varient implements Parcelable, Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("product")
    @Expose
    private String product;
    @SerializedName("attributes")
    @Expose
    private List<Attribute> attributes = null;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("price")
    @Expose
    private float price;
    @SerializedName("sellingPrice")
    @Expose
    private float sellingPrice;

    @SerializedName("stock")
    @Expose
    private Integer stock;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("reviews")
    @Expose
    private Reviews reviews;

    @SerializedName("wishlist")
    @Expose
    private Boolean wishlist;

    @SerializedName("inCart")
    @Expose
    private Long inCart;

    @SerializedName("minOrderQuantity")
    private Integer minOrderQuantity;

    public Integer getMinOrderQuantity() {
        return minOrderQuantity;
    }

    public void setMinOrderQuantity(Integer minOrderQuantity) {
        this.minOrderQuantity = minOrderQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Reviews getReviews() {
        return reviews;
    }

    public void setReviews(Reviews reviews) {
        this.reviews = reviews;
    }

    public Long getInCart() {
        return inCart;
    }

    public void setInCart(Long inCart) {
        this.inCart = inCart;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.product);
        dest.writeTypedList(this.attributes);
        dest.writeString(this.description);
        dest.writeValue(this.price);
        dest.writeValue(this.sellingPrice);
        dest.writeValue(this.stock);
        dest.writeValue(this.wishlist);
        dest.writeParcelable(this.images, flags);
        dest.writeParcelable(this.reviews, flags);
        dest.writeValue(this.minOrderQuantity);
        dest.writeLong(this.inCart);
    }

    public Varient() {
    }

    protected Varient(Parcel in) {
        this.id = in.readString();
        this.product = in.readString();
        this.attributes = in.createTypedArrayList(Attribute.CREATOR);
        this.description = in.readString();
        this.wishlist = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.price = (Float) in.readValue(Integer.class.getClassLoader());
        this.sellingPrice = (Float) in.readValue(Integer.class.getClassLoader());
        this.stock = (Integer) in.readValue(Integer.class.getClassLoader());
        this.images = in.readParcelable(Images.class.getClassLoader());
        this.reviews = in.readParcelable(Reviews.class.getClassLoader());
        this.minOrderQuantity = in.readParcelable(Integer.class.getClassLoader());
        this.inCart = in.readLong();
    }

    public static final Parcelable.Creator<Varient> CREATOR = new Parcelable.Creator<Varient>() {
        @Override
        public Varient createFromParcel(Parcel source) {
            return new Varient(source);
        }

        @Override
        public Varient[] newArray(int size) {
            return new Varient[size];
        }
    };

    public float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Boolean getWishlist() {
        return wishlist;
    }

    public void setWishlist(Boolean wishlist) {
        this.wishlist = wishlist;
    }
}
