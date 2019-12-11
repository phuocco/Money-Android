package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
    TextInputEditText mEditTextEmail, mEditTextPassword;
    CardView mButtonLogin;
    TextView mLoginRegister;
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
        mEditTextEmail =findViewById(R.id.login_email);
        mEditTextPassword = findViewById(R.id.login_password);
        mButtonLogin = findViewById(R.id.button_login);
        mLoginRegister = findViewById(R.id.login_register);
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(mEditTextEmail.getText().toString(), mEditTextPassword.getText().toString());
            }
        });
        mLoginRegister.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(this, "Fill password", Toast.LENGTH_SHORT).show();
            return;
        }
    compositeDisposable.add(myService.loginUser(email,password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String response) throws Exception {
                    response= response.replace("\"", "");
                    String inputEmail = mEditTextEmail.getText().toString();
                    String responseInvalid = "Invalid";
                    if(responseInvalid.equals(response)) {
                        Toast.makeText(LoginActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(EMAIL,inputEmail);
                        editor.apply();
                    }
                }

            }));

    }
}
