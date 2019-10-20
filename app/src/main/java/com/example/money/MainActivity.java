package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.money.Home.ChartActivity;
import com.example.money.Home.HomeActivity;
import com.example.money.Home.TransactionCategoryActivity;
import com.example.money.Transaction.AddExpenseActivity;
import com.example.money.Transaction.AddIncomeActivity;
import com.example.money.Transaction.DetailTransactionActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button login, reg,ex,home,in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.main_login);
        reg = findViewById(R.id.main_reg);
        ex = findViewById(R.id.buttonex);
        in = findViewById(R.id.buttonin);
        home = findViewById(R.id.buttonhome);

        startActivity(new Intent(MainActivity.this, ChartActivity.class));
        login.setOnClickListener(this);
        reg.setOnClickListener(this);
        ex.setOnClickListener(this);
        in.setOnClickListener(this);
        home.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_login:
                Intent a = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(a);
                break;
            case R.id.main_reg:
                Intent b = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(b);
                break;
            case R.id.buttonex:
                Intent c = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivity(c);
                break;
            case R.id.buttonin:
                Intent d = new Intent(MainActivity.this, ChartActivity.class);
                startActivity(d);
                break;
            case R.id.buttonhome:
                Intent e = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(e);
                break;
        }
    }
}
