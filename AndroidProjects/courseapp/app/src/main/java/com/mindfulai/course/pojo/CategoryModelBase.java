package com.mindfulai.course.pojo;

import java.util.ArrayList;

public class CategoryModelBase {
    private int status;
    private ArrayList<BrowseServiceModel> data;
    private boolean errors;
    private String message;

    public CategoryModelBase() {
    }

    public CategoryModelBase(int status, ArrayList<BrowseServiceModel> data, boolean errors, String message) {
        this.status = status;
        this.data = data;
        this.errors = errors;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isErrors() {
        return errors;
    }

    public void setErrors(boolean errors) {
        this.errors = errors;
    }

    public ArrayList<BrowseServiceModel> getData() {
        return data;
    }

    public void setData(ArrayList<BrowseServiceModel> data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
