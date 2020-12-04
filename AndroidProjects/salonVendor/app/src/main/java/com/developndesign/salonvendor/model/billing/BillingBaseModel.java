package com.developndesign.salonvendor.model.billing;

import java.util.ArrayList;

public class BillingBaseModel {
    private int status;
    private ArrayList<BillingDataModel> data;
    private String message;
    private boolean errors;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<BillingDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<BillingDataModel> data) {
        this.data = data;
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
}
