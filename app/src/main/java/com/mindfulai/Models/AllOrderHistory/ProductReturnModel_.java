
package com.mindfulai.Models.AllOrderHistory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductReturnModel_ implements Parcelable {

    @SerializedName("images")
    @Expose
    private Images images;
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
    @SerializedName("sellingPrice")
    @Expose
    private Float sellingPrice;
    @SerializedName("stock")
    @Expose
    private Integer stock;

    public Float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Float sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.images, flags);
        dest.writeString(this.id);
        dest.writeParcelable(this.product, flags);
        dest.writeTypedList(this.attributes);
        dest.writeString(this.description);
        dest.writeValue(this.price);
        dest.writeValue(this.sellingPrice);
        dest.writeValue(this.stock);
    }

    public ProductReturnModel_() {
    }

    protected ProductReturnModel_(Parcel in) {
        this.images = in.readParcelable(Images.class.getClassLoader());
        this.id = in.readString();
        this.product = in.readParcelable(Product__.class.getClassLoader());
        this.attributes = in.createTypedArrayList(Attribute.CREATOR);
        this.description = in.readString();
        this.price = (Float) in.readValue(Integer.class.getClassLoader());
        this.sellingPrice = (Float) in.readValue(Integer.class.getClassLoader());
        this.stock = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ProductReturnModel_> CREATOR = new Parcelable.Creator<ProductReturnModel_>() {
        @Override
        public ProductReturnModel_ createFromParcel(Parcel source) {
            return new ProductReturnModel_(source);
        }

        @Override
        public ProductReturnModel_[] newArray(int size) {
            return new ProductReturnModel_[size];
        }
    };
}
