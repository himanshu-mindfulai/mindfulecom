package com.mindfulai.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mindfulai.Models.varientsByCategory.Attribute;
import com.mindfulai.Models.varientsByCategory.Images;
import com.mindfulai.Models.varientsByCategory.Product;

import java.util.List;

public class ProductWishList implements Parcelable {
    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("product")
    @Expose
    private WishProductModel product;

    @SerializedName("attributes")
    @Expose
    private List<Attribute> attribute;


    @SerializedName("stock")
    @Expose
    private Integer stock;

    @SerializedName("price")
    @Expose
    private Integer price;

    public ProductWishList(String _id, WishProductModel product, List<Attribute> attribute, Integer stock, Integer price, Images images, String description) {
        this._id = _id;
        this.product = product;
        this.attribute = attribute;
        this.stock = stock;
        this.price = price;
        this.images = images;
        this.description = description;
    }

    private ProductWishList(Parcel in) {
        _id = in.readString();
        description = in.readString();
        product = in.readParcelable(Product.class.getClassLoader());
        attribute = in.readParcelable(Attribute.class.getClassLoader());
        if (in.readByte() == 0) {
            stock = null;
        } else {
            stock = in.readInt();
        }
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readInt();
        }
        images = in.readParcelable(Images.class.getClassLoader());
    }

    public static final Creator<ProductWishList> CREATOR = new Creator<ProductWishList>() {
        @Override
        public ProductWishList createFromParcel(Parcel in) {
            return new ProductWishList(in);
        }

        @Override
        public ProductWishList[] newArray(int size) {
            return new ProductWishList[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public WishProductModel getProduct() {
        return product;
    }

    public void setProduct(WishProductModel product) {
        this.product = product;
    }

    public List<Attribute> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<Attribute> attribute) {
        this.attribute = attribute;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @SerializedName("images")
    @Expose
    private Images images;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(description);
        dest.writeParcelable(product, flags);
        dest.writeTypedList(attribute);
        if (stock == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(stock);
        }
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(price);
        }
        dest.writeParcelable(images, flags);
    }
}
