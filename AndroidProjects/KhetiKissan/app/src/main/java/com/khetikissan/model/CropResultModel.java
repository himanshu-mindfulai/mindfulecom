package com.khetikissan.model;

import java.util.ArrayList;

public class CropResultModel {
    private ArrayList<CropModel> Result;

    public ArrayList<CropModel> getCropModelArrayList() {
        return Result;
    }

    public void setCropModelArrayList(ArrayList<CropModel> cropModelArrayList) {
        this.Result = cropModelArrayList;
    }
}
