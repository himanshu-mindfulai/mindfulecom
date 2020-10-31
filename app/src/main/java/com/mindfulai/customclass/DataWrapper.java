package com.mindfulai.customclass;

import com.mindfulai.Models.varientsByCategory.Datum;

import java.io.Serializable;
import java.util.List;

public class DataWrapper implements Serializable {

    private List<Datum> varientList;

    public DataWrapper(List<Datum> data) {
        this.varientList = data;
    }


    public List<Datum> getVarientList() {
        return this.varientList;
    }

}