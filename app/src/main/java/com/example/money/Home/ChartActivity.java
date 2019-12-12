package com.example.money.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChartActivity extends AppCompatActivity {
    PieChart pieChart;
    MyService myService;
    TextView mSelectTime;
    RadioGroup mRadioGroupChart;
    RadioButton mRadioButtonInCome, mRadioButtonExpense;
    boolean isDateNoSelected = true;
    boolean isDateSelected = true;
    private boolean isDark =  false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //retrofit
        Retrofit retrofitClient = RetrofitClient.getInstance();
        //init
        init();
        myService = retrofitClient.create(MyService.class);

        isDark = getThemeStatePref();
        if (isDark){
            getWindow().getDecorView().setBackgroundResource(R.drawable.gradient_main_dark);
            mSelectTime.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            getWindow().getDecorView().setBackgroundResource(R.drawable.gradient_main);
        }
        final Calendar calendar = Calendar.getInstance();
        final int month = calendar.get(Calendar.MONTH)+1;
        final int year = calendar.get(Calendar.YEAR);
        String type = "Expense";

        mSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });
        String selectMonth = String.valueOf(month);
        String selectYear= String.valueOf(year);
        getChartByMonth(selectYear,selectMonth,type);
        //set default
        mRadioButtonInCome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDateNoSelected){
                    String type2 = "Income";
                    getChartByMonth(String.valueOf(year),String.valueOf(month),type2);
                }

            }
        });
        mRadioButtonExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDateSelected){
                    String type2 = "Expense";
                   // getChartByMonth(selectYear,String.valueOf(month),type2);
                }
            }
        });
    }

    private void init() {
        pieChart =  findViewById(R.id.chart);
        mSelectTime =  findViewById(R.id.chart_select_time);
        mRadioGroupChart = findViewById(R.id.radio_chart);
        mRadioButtonInCome = findViewById(R.id.radio_income);
        mRadioButtonExpense = findViewById(R.id.radio_expense);
    }

    private void pickDate() {
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i,i1,i2);
               // Toast.makeText(ChartActivity.this, "year: "+i + "   month "+(i1+1), Toast.LENGTH_SHORT).show();
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM", Locale.US);

                isDateNoSelected = false;
                isDateNoSelected = false;

                String date = simpleDateFormat.format(calendar.getTime());
                mSelectTime.setText(getString(R.string.selected_month, date));
                int radioId = mRadioGroupChart.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)findViewById(radioId);
                String type = radioButton.getText().toString();
                String selectYear = String.valueOf(i);
                String selectMonth =  String.valueOf((i1+1));
                getChartByMonth(selectYear,selectMonth,type);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    // call api (select year, select month, type)
    private void getChartByMonth(String selectYear, String selectMonth,String type ) {
        final Chart chart = new Chart(selectMonth,selectYear,type);
        myService.getChartByMonth(chart)
                .enqueue(new Callback<List<Chart>>() {
                    @Override
                    public void onResponse(Call<List<Chart>> call, Response<List<Chart>> response) {

                        if (response.isSuccessful()) {
                        List<Chart> list = response.body();
                        ArrayList NoOfEmp = new ArrayList();
                        float n;
                        int size = list.size();
                        for(int i=0;i<size;i++){
                            n =  Math.abs(list.get(i).getSum());
                            NoOfEmp.add(new PieEntry(n, list.get(i).getId()));
                        }

                        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Months");
                        pieChart.getDescription().setText("Summary all month");
                        PieData data = new PieData(dataSet);
                        dataSet.setValueTextSize(15f);
                        pieChart.setData(data);
                        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        pieChart.animateXY(3000, 3000);



                        }
                    }
                    @Override
                    public void onFailure(Call<List<Chart>> call, Throwable t) {

                    }
                });
    }
    private boolean getThemeStatePref(){
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        isDark = preferences.getBoolean(Constants.ISDARK,false);
        return isDark;
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
