package com.example.money.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.money.Constants;
import com.example.money.LoginActivity;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.SettingsActivity;
import com.example.money.models.Transaction;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private MyService myService;
    TextView mTitle;
    ImageButton mSelectMonthButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ThisMonthFragment()).commit();
            navigationView.setCheckedItem(R.id.thismonth);
        }
        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        View headerView = navigationView.getHeaderView(0);
        mTitle = headerView.findViewById(R.id.header_email);
        mTitle.setText(email);

        //select month
        mSelectMonthButton =  findViewById(R.id.action_bar_button);
        mSelectMonthButton.setOnClickListener(new View.OnClickListener() {
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
                        String selectYear = String.valueOf(i);
                        String selectMonth =  String.valueOf((i1+1));
                        Fragment monthFragment =  new MonthFragment();
                        Bundle data = new Bundle();
                        data.putString("month",selectMonth);
                        data.putString("year",selectYear);
                        monthFragment.setArguments(data);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, monthFragment).commit();

                    }
                },year,(month-1),day);
                datePickerDialog.show();
            }
        });

        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_range:
//               // startActivity(new Intent(HomeActivity.this, RangeActivity.class));
//
//        }
        if(item.getItemId()==R.id.menu_range){
            showRangeDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRangeDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_range, null);

        final TextView startDate =  dialogView.findViewById(R.id.range_startDate);
        final TextView endDate =  dialogView.findViewById(R.id.range_endDate);
        final Button rangeOK =  dialogView.findViewById(R.id.range_ok);
        final Button  rangeCancel =  dialogView.findViewById(R.id.range_cancel);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mDayStart =  calendar.get(Calendar.DATE);
                int mMonthStart = calendar.get(Calendar.MONTH);
                int mYearStart = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        String date = simpleDateFormat.format(calendar.getTime());
                        startDate.setText(date);
                    }
                },mYearStart,mMonthStart,mDayStart);
                datePickerDialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mDayEnd =  calendar.get(Calendar.DATE);
                int mMonthEnd = calendar.get(Calendar.MONTH);
                int mYearEnd = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("dd-MM-yyyy",Locale.US);
                        String date = simpleDateFormat.format(calendar.getTime());
                        endDate.setText(date);
                    }
                },mYearEnd,mMonthEnd,mDayEnd);
                datePickerDialog.show();
            }
        });
        rangeOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = startDate.getText().toString();
                String end = endDate.getText().toString();
                String startDateStr = null;
                String endDateStr = null;
                try {
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy",Locale.US);
                    Date mDateStart = formatter.parse(start);
                    startDateStr = String.valueOf(mDateStart.getTime());
                    Date mDateEnd = formatter.parse(end);
                    endDateStr = String.valueOf(mDateEnd.getTime());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                Fragment rangeFragment =  new RangeFragment();
                Bundle data = new Bundle();
                data.putString("startDate",startDateStr);
                data.putString("endDate",endDateStr);
                rangeFragment.setArguments(data);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, rangeFragment).commit();
                dialogBuilder.dismiss();
            }
        });
        rangeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCanceledOnTouchOutside(false);
        dialogBuilder.show();

    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
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
            case R.id.sign_out:
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
