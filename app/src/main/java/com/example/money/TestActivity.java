package com.example.money;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TestActivity extends AppCompatActivity {
    TextView text_test;
    MyService myService;
    RetrofitClient retrofitClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //retrofit
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        Transaction transaction = new Transaction(email);
        myService.getAll(transaction).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
               if(response.isSuccessful()){
                 //  Toast.makeText(TestActivity.this, ""+response.toString(), Toast.LENGTH_SHORT).show();

                final   List<Transaction> transactionList =  response.body();
                   int sum = 1;
                   for (Transaction a: transactionList){
                       sum = sum + Integer.parseInt(a.getAmount());
                   }
                   Toast.makeText(TestActivity.this, ""+sum, Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
