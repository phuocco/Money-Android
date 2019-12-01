package com.example.money.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Quotes {

    @SerializedName("USDVND")
    @Expose
    private Double uSDVND;

    public Double getUSDVND() {
        return uSDVND;
    }

    public void setUSDVND(Double uSDVND) {
        this.uSDVND = uSDVND;
    }

}

