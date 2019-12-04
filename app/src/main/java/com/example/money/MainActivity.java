package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.example.money.Home.HomeActivity;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.Transaction.AddExpenseActivity;
import com.example.money.Transaction.AddIncomeActivity;
import com.example.money.models.Quotes;



public class MainActivity extends AppCompatActivity{

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "email";
    private MyService myService;
    RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        login = findViewById(R.id.main_login);
//        reg = findViewById(R.id.main_reg);
//        ex = findViewById(R.id.buttonex);
//        in = findViewById(R.id.buttonin);
//        home = findViewById(R.id.buttonhome);
//        tv = findViewById(R.id.textView2);

        SharedPreferences sharedPreferences =  getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL,"").replace("\"", "");
        email = email.replace("\"", "");
        if(email.isEmpty()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


       // startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//        login.setOnClickListener(this);
//        reg.setOnClickListener(this);
//        ex.setOnClickListener(this);
//        in.setOnClickListener(this);
//        home.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.main_login:
//                Intent a = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(a);
//                break;
//            case R.id.main_reg:
//                Intent b = new Intent(MainActivity.this, RegisterActivity.class);
//                startActivity(b);
//                break;
//            case R.id.buttonex:
//                Intent c = new Intent(MainActivity.this, AddExpenseActivity.class);
//                startActivity(c);
//                break;
//            case R.id.buttonin:
//                Intent d = new Intent(MainActivity.this, AddIncomeActivity.class);
//                startActivity(d);
//                break;
//            case R.id.buttonhome:
//                Intent e = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(e);
//                break;
//        }
//    }
}
