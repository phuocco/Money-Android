package com.example.money.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Chart;
import com.example.money.models.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;
    MyService myService;
    RetrofitClient retrofitClient;
    TextView selectTime,chartTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //retrofit
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);

        chartTemp = findViewById(R.id.chart_temp);
        selectTime =  findViewById(R.id.chart_select_time);
        final Calendar calendar = Calendar.getInstance();
        final  int month = calendar.get(Calendar.MONTH)+1;
        final  int year = calendar.get(Calendar.YEAR);
        Toast.makeText(this, ""+(month) + "  "+ year, Toast.LENGTH_SHORT).show();
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });
        selectTime.setText(month + " "+year);
        String selectMonth = String.valueOf(month);
        String selectYear= String.valueOf(year);
        Chart chart = new Chart(selectMonth,selectYear);
        myService.getChartByMonth(chart)
                .enqueue(new Callback<List<Chart>>() {
                    @Override
                    public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {

                        chartTemp.setText(response.toString());
                        List<PieEntry> pieEntries =  new ArrayList<>();
                        for (Chart chart: response.body()){
                            pieEntries.add(new PieEntry(chart.getSum(),chart.getId()));
                        }
                        PieDataSet pieDataSet = new PieDataSet(pieEntries,"Sum per cate");
                        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                        PieData pieData = new PieData(pieDataSet);
                        pieChart = findViewById(R.id.chart);
                        pieChart.setData(pieData);
                        Description description = new Description();
                        description.setText("aaa");
                        pieChart.setDescription(description);
                        pieChart.invalidate();
                    }
                    @Override
                    public void onFailure(Call<List<Chart>> call, Throwable t) {

                    }
                });


       // getChartByMonth(selectMonth,selectYear);
    }

    private void pickDate() {
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final  int month = calendar.get(Calendar.MONTH) + 1;
        final  int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i,i1,i2);
                Toast.makeText(ChartActivity.this, "year: "+i + "   month "+(i1+1), Toast.LENGTH_SHORT).show();
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM");
                selectTime.setText(simpleDateFormat.format(calendar.getTime()));
                String selectYear = String.valueOf(i);
                String selectMonth =  String.valueOf((i1+1));
                getChartByMonth(selectYear,selectMonth);
            }
        },year,month,day);
        datePickerDialog.show();
    }



    private void getChartByMonth(String selectYear, String selectMonth) {
        Chart chart = new Chart(selectMonth,selectYear);
        myService.getChartByMonth(chart)
                .enqueue(new Callback<List<Chart>>() {
                    @Override
                    public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {

                        chartTemp.setText(response.toString());
                        List<PieEntry> pieEntries =  new ArrayList<>();
                        for (Chart chart: response.body()){
                            pieEntries.add(new PieEntry(chart.getSum(),chart.getId()));
                        }
                        PieDataSet pieDataSet = new PieDataSet(pieEntries,"Sum per cate");
                        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                        PieData pieData = new PieData(pieDataSet);
                        pieChart = findViewById(R.id.chart);
                        pieChart.setData(pieData);
                        Description description = new Description();
                        description.setText("aaa");
                        pieChart.setDescription(description);

                        pieChart.invalidate();
                    }
                    @Override
                    public void onFailure(Call<List<Chart>> call, Throwable t) {

                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
