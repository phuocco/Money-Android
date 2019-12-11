package com.example.money.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chart {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("sum")
    @Expose
    private Float sum;


    private String month;
    private String year;
    private String type;

    public Chart(String month, String year, String type) {
        this.month = month;
        this.year = year;
        this.type = type;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Float getSum() {
        return sum;
    }
    public void setSum(Float sum) {
        this.sum = sum;
    }



    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }


    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
