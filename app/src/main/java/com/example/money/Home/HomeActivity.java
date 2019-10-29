package com.example.money.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.SettingsActivity;
import com.example.money.models.Transaction;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private MyService myService;
    RetrofitClient retrofitClient;
    TextView title;
    ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ThisMonthFragment()).commit();
            navigationView.setCheckedItem(R.id.thismonth);
        }
        //select month
        imageButton =  findViewById(R.id.action_bar_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final  int month = calendar.get(Calendar.MONTH) + 1;
                final  int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        calendar.set(i,i1,i2);
                       // Toast.makeText(HomeActivity.this, "year: "+i + "   month "+(i1+1), Toast.LENGTH_SHORT).show();
                       // SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM");
                        String selectYear = String.valueOf(i);
                        String selectMonth =  String.valueOf((i1+1));
                        Fragment monthFragment =  new MonthFragment();

                        Bundle data = new Bundle();
                        data.putString("month",selectMonth);
                        data.putString("year",selectYear);
                        monthFragment.setArguments(data);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, monthFragment).commit();

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });





        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        Transaction transaction =  new Transaction(email);
//        myService.getAll(transaction)
//                .enqueue(new Callback<List<Transaction>>() {
//                    @Override
//                    public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
//                        if(response.isSuccessful()){
//                            Toast.makeText(HomeActivity.this, "asdasd", Toast.LENGTH_SHORT).show();
//
//                            int sum = 0;
//                            for (Transaction transaction1: response.body()){
//
//                                sum = sum + Integer.parseInt(transaction1.getAmount());
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<Transaction>> call, Throwable t) {
//
//                    }
//                });


    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.thismonth:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ThisMonthFragment()).commit();
                break;
            case R.id.lastmonth:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LastMonthFragment()).commit();
                break;
            case R.id.plan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PlanFragment()).commit();
                break;
            case R.id.nav_a:
                startActivity(new Intent(HomeActivity.this,ChartActivity.class));
                break;
            case R.id.settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
