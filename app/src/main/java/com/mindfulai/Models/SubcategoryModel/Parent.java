package com.mindfulai.Models.SubcategoryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parent {

@SerializedName("is_active")
@Expose
private Boolean isActive;
@SerializedName("_id")
@Expose
private String id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("type")
@Expose
private String type;
@SerializedName("image")
@Expose
private String image;

public Boolean getIsActive() {
return isActive;
}

public void setIsActive(Boolean isActive) {
this.isActive = isActive;
}

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
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

public String getImage() {
return image;
}

public void setImage(String image) {
this.image = image;
}

}