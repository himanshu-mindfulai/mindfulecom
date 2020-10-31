
package com.mindfulai.Models.BestSellingData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {

    @SerializedName("attribute")
    @Expose
    private Attribute_ attribute;
    @SerializedName("option")
    @Expose
    private Option option;

    public Attribute_ getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute_ attribute) {
        this.attribute = attribute;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

}
