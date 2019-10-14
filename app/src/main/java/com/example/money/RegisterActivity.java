package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    Button button_reg;
    EditText reg_email, reg_username, reg_password;
    String email,username,password;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();
    MyService myService;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        reg_email = findViewById(R.id.reg_email);
        reg_username = findViewById(R.id.reg_username);
        reg_password = findViewById(R.id.reg_password);
        button_reg =  findViewById(R.id.button_reg);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(reg_email.getText().toString(),reg_username.getText().toString(), reg_password.getText().toString());
            }
        });
    }

    private void registerUser(String email, String username, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Fill email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Fill email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Fill email", Toast.LENGTH_SHORT).show();
        }


        compositeDisposable.add(myService.registerUser(email, username, password)
                        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                    }
                })
        );

    }
}
