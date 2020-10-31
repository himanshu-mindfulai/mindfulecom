package com.mindfulai.Models.ProductVarients;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

@SerializedName("total")
@Expose
private Integer total;
@SerializedName("rating")
@Expose
private Float rating;

public Integer getTotal() {
return total;
}

public void setTotal(Integer total) {
this.total = total;
}

public Float getRating() {
return rating;
}

public void setRating(Float rating) {
this.rating = rating;
}

}