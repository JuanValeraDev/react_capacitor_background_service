package com.react_capacitor_background_service;

import com.google.gson.annotations.SerializedName;

public class CurrentData {
    @SerializedName("temperature_2m")
    private double temp;

    public double getTemp() {
        return temp;
    }
}
