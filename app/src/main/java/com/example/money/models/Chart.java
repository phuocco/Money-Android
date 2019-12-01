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

//    @SerializedName("month")
//    @Expose
    private String month;

//    @SerializedName("month")
//    @Expose
    private String year;

    public Chart(String month, String year) {
        this.month = month;
        this.year = year;
    }



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
