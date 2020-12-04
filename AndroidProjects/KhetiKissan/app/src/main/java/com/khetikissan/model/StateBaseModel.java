package com.khetikissan.model;

import java.util.ArrayList;

public class StateBaseModel {

    private ArrayList<StateModel> States;

    public ArrayList<StateModel> getState() {
        return States;
    }

    public void setState(ArrayList<StateModel> state) {
        this.States = state;
    }
}
