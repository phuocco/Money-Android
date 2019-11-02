package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Home.HomeActivity;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText ed_email,ed_password;
    CardView button_login;
    TextView login_register;
    CompositeDisposable compositeDisposable  = new CompositeDisposable();
    MyService myService;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "email";
    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        ed_email=findViewById(R.id.login_email);
        ed_password = findViewById(R.id.login_password);
        button_login = findViewById(R.id.button_login);
        login_register = findViewById(R.id.login_register);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(ed_email.getText().toString(), ed_password.getText().toString());
            }
        });
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }

    private void loginUser(final String email, String password) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Fill email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Fill email", Toast.LENGTH_SHORT).show();
            return;
        }
    compositeDisposable.add(myService.loginUser(email,password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String response) throws Exception {
                        response= response.replace("\"", "");
                        if(response.equals(ed_email.getText().toString())){
                        Toast.makeText(LoginActivity.this, "success :"+response, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(EMAIL,response);
                        editor.apply();
                    } else {
                        Toast.makeText(LoginActivity.this, "wrong pass", Toast.LENGTH_SHORT).show();
                    }


                //    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
            }));
    }
}
