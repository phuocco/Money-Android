package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddIncomeActivity extends AppCompatActivity {
    EditText ed_email,ed_amount,ed_note, ed_category,ed_type;
    Button button_add_in;
    MyService myService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);

        ed_email =  findViewById(R.id.add_in_email);
        ed_amount =findViewById(R.id.add_in_amount);
        ed_note =findViewById(R.id.add_in_note);
        ed_category =findViewById(R.id.add_in_category);
        ed_type =findViewById(R.id.add_in_type);
        button_add_in = findViewById(R.id.button_add_in);

        button_add_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ed_email.getText().toString();
                String amount = ed_amount.getText().toString();
                String note = ed_note.getText().toString();
                String category = ed_category.getText().toString();
                String type =  ed_type.getText().toString();
                addTransaction(email,amount,note,category,type);
            }
        });

    }

    private void addTransaction(String email, String amount, String note, String category, String type) {
        myService.addTransaction(email,amount,note,category,type)
                .enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(AddIncomeActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {

                    }
                });
    }
}
