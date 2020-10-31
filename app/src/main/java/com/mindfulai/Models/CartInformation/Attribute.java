
package com.mindfulai.Models.CartInformation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribute {

    @SerializedName("option")
    @Expose
    private Option option;
    @SerializedName("attribute")
    @Expose
    private Attribute_ attribute;

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Attribute_ getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute_ attribute) {
        this.attribute = attribute;
    }

}
