package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailTransactionActivity extends AppCompatActivity {
    TextView id,amount,note,category;
    MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);
        id = findViewById(R.id.detail_id);
        amount = findViewById(R.id.detail_amount);
        note = findViewById(R.id.detail_note);
        category = findViewById(R.id.detail_category);

        //get id
        Intent intent = getIntent();
        String transactionId = intent.getStringExtra("TransactionID");
        Toast.makeText(this, ""+transactionId, Toast.LENGTH_SHORT).show();

        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        Call<Transaction> call = myService.getTransactionById(transactionId);
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                Transaction transaction = response.body();
                id.setText(transaction.getId());
                amount.setText(transaction.getAmount());
                note.setText(transaction.getNote());
                category.setText(transaction.getCategory());
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Toast.makeText(DetailTransactionActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
