package com.mindfulai.Models.varientsByCategory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class OptionsAttribute implements Parcelable {
    private String name;
    private ArrayList<String> value;

    public OptionsAttribute() {
    }

    public OptionsAttribute(String name, ArrayList<String> value) {
        this.name = name;
        this.value = value;
    }

    private OptionsAttribute(Parcel in) {
        name = in.readString();
        value = in.createStringArrayList();
    }

    public static final Creator<OptionsAttribute> CREATOR = new Creator<OptionsAttribute>() {
        @Override
        public OptionsAttribute createFromParcel(Parcel in) {
            return new OptionsAttribute(in);
        }

        @Override
        public OptionsAttribute[] newArray(int size) {
            return new OptionsAttribute[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getValue() {
        return value;
    }

    public void setValue(ArrayList<String> value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(value);

    }
}
