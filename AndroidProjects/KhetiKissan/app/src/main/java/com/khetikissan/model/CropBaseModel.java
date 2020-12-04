package com.khetikissan.model;

import java.util.ArrayList;

public class CropBaseModel {
    private ArrayList<CropModel> Crops;

    public ArrayList<CropModel> getCropModelArrayList() {
        return Crops;
    }

    public void setCropModelArrayList(ArrayList<CropModel> cropModelArrayList) {
        this.Crops = cropModelArrayList;
    }
}
