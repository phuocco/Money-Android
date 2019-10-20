package com.example.money.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chart {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("sum")
    @Expose
    private Integer sum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
