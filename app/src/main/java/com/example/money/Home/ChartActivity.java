package com.example.money.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;
    MyService myService;
    RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getChart();
    }

    private void getChart() {
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        Call<List<Chart>> call = myService.getChart();
        call.enqueue(new Callback<List<Chart>>() {
            @Override
            public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {
                List<PieEntry> pieEntries =  new ArrayList<>();
                for (Chart chart: response.body()){
                    pieEntries.add(new PieEntry(chart.getSum(),chart.getId()));
                }

                PieDataSet pieDataSet = new PieDataSet(pieEntries,"Sum per cate");
                pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                PieData pieData = new PieData(pieDataSet);
                pieChart = findViewById(R.id.chart);

              //  pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                
                pieChart.setData(pieData);
                Description description = new Description();
                description.setText("aaa");
                pieChart.setDescription(description);

                pieChart.setDrawHoleEnabled(true);
                pieChart.setHoleRadius(50f);

                // enable rotation of the chart by touch
                pieChart.setRotationAngle(0);
                pieChart.setRotationEnabled(false);
                pieChart.setDrawCenterText(true);


                pieChart.invalidate();
            }

            @Override
            public void onFailure(Call<List<Chart>> call, Throwable t) {

            }
        });
    }
}
