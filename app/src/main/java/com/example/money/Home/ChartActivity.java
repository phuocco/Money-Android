package com.example.money.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
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
    private float[] yData ={};
    private String[] xData = {};

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
        pieChart =  findViewById(R.id.chart);
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
        String selectMonth = String.valueOf(month);
        String selectYear= String.valueOf(year);
        getChartByMonth(selectYear,selectMonth);


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
               // Toast.makeText(ChartActivity.this, "year: "+i + "   month "+(i1+1), Toast.LENGTH_SHORT).show();
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM");


                String date = simpleDateFormat.format(calendar.getTime());
                selectTime.setText(getString(R.string.selected_month, date));

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

                        if (response.isSuccessful()) {

                            List<Chart> list = response.body();
                            ArrayList<PieEntry> yEntries =  new ArrayList<>();
                            ArrayList<String> xEntries = new ArrayList<>();
                            for(int i =0; i<list.size();i++){
                                float y = (float)list.get(i).getSum();
                                yEntries.add(new PieEntry(y,i));
                            }
                            for (int i = 1;i<list.size();i++){
                                xEntries.add(list.get(i).getId());
                            }
                             PieDataSet pieDataSet = new PieDataSet(yEntries,"Avg category");
                            pieDataSet.setSliceSpace(2);
                            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                            PieData pieData = new PieData(pieDataSet);
                            pieChart.setData(pieData);
                            pieChart.invalidate();
//                            ArrayList<PieEntry> entries = new ArrayList<>();
//                            for (int i = 0; i < list.size(); i++) {
//                                entries.add(new PieEntry((float) list.get(i).getSum(), list.get(i).getId()));
//                            }
//                            PieDataSet dataSet = new PieDataSet(entries, "Election Results");
//                            dataSet.setDrawIcons(false);
//                            dataSet.setSliceSpace(3f);
//                            dataSet.setSelectionShift(5f);
//                            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                            PieData data = new PieData();
//                            data.setValueTextSize(11f);
//                            data.setValueTextColor(Color.WHITE);
//                            pieChart.setData(data);


//                        ArrayList NoOfEmp = new ArrayList();
//
//                        NoOfEmp.add(new PieEntry(945f, 0));
//                        NoOfEmp.add(new PieEntry(1040f, 1));
//                        NoOfEmp.add(new PieEntry(1133f, 2));
//                        NoOfEmp.add(new PieEntry(1240f, 3));
//                        NoOfEmp.add(new PieEntry(1369f, 4));
//                        NoOfEmp.add(new PieEntry(1487f, 5));
//                        NoOfEmp.add(new PieEntry(1501f, 6));
//                        NoOfEmp.add(new PieEntry(1645f, 7));
//                        NoOfEmp.add(new PieEntry(1578f, 8));
//                        NoOfEmp.add(new PieEntry(1695f, 9));
//                        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Number Of Employees");
//
//                        PieData data = new PieData(dataSet);
//                        Log.d("piedataset", dataSet.toString());
//                        Log.d("piedata", data.toString());
//                        pieChart.setData(data);
//                        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                        pieChart.animateXY(5000, 5000);


//                        List<Chart> list =  response.body();
//                        List<PieEntry> entries = new ArrayList<>();
//                        for(int i = 0;i<list.size();i++){
//                            float value = (float)list.get(i).getSum();
//                            entries.add(new PieEntry(value,list.get(i).getId()));
//                        }
//
//                        ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
//                       for(int i =0;i<list.size();i++){
//                           float value = (float)list.get(i).getSum();
//                           yvalues.add(new PieEntry(value,0));
//                       }
//
//                        PieDataSet dataSet = new PieDataSet(yvalues, "Election Results");
//
//                        ArrayList<String> xVals = new ArrayList<String>();
//
//                        for(int i =0;i<list.size();i++){
//                            xVals.add(list.get(i).getId());
//                        }
//
//                        PieData data = new PieData(dataSet);
//                        pieChart.setData(data);

//chart default
//                        ArrayList<PieEntry> pieEntries =  new ArrayList<>();
//                        for (Chart chart: response.body()){
//                            pieEntries.add(new PieEntry(chart.getSum(),chart.getId()));
//                        }
//
//                        pieChart.setHoleRadius(35f);
//                        pieChart.setTransparentCircleAlpha(0);
//                        pieChart.setCenterText("PieChart");
//                        pieChart.setCenterTextSize(10);
//
//                        pieChart.setDrawEntryLabels(true);
//                        PieDataSet pieDataSet=new PieDataSet(pieEntries,"Employee Sales");
//                        pieDataSet.setSliceSpace(2);
//                        pieDataSet.setValueTextSize(12);
//                        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//
//                        PieData pieData=new PieData(pieDataSet);
//                        pieChart.setData(pieData);
//                        Log.d("test",pieData.toString());
//                        pieChart.invalidate();
// quit

//                        PieDataSet pieDataSet = new PieDataSet(pieEntries,"Sum per cate");
//                        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//                        PieData pieData = new PieData(pieDataSet);
//                        pieChart = findViewById(R.id.chart);
//                        pieChart.setData(pieData);
//                        Description description = new Description();
//                        description.setText("aaa");
//                        pieChart.setDescription(description);
//                        pieChart.invalidate();
                        }
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
