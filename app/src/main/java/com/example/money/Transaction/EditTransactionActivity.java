package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.Home.HomeActivity;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditTransactionActivity extends AppCompatActivity {
    TextInputEditText eamount,ecategory,enote,edate;
    MyService myService;
    MaterialButton button_edit;
    TextView tv_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        eamount = findViewById(R.id.edit_amount);
        enote = findViewById(R.id.edit_note);
        ecategory =  findViewById(R.id.edit_category);
        edate = findViewById(R.id.edit_date);
        tv_type =  findViewById(R.id.edit_type);
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
       final String transactionId = sharedPreferences.getString(Constants.ID,"").replace("\"", "");
        //final String transactionId = "5dac689c788f27175c88b5f4";
        Toast.makeText(EditTransactionActivity.this, ""+transactionId, Toast.LENGTH_SHORT).show();

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_edit = findViewById(R.id.button_edit);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount =  eamount.getText().toString();
                String note =  enote.getText().toString();
                String date =  edate.getText().toString();
                String category =  ecategory.getText().toString();
                String type = tv_type.getText().toString();
                Toast.makeText(EditTransactionActivity.this, ""+transactionId, Toast.LENGTH_SHORT).show();
                updateTrans(transactionId,amount,category,type,note,date);
            }
        });

        insertTransaction(transactionId);

    }

    private void updateTrans(String transactionId, String amount,String category, String type,String note, String date) {
        myService.updateTransaction(transactionId,amount,category,type,note,date)
                .enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(EditTransactionActivity.this, "okkkk", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditTransactionActivity.this, HomeActivity.class));

                        }
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {

                    }
                });
    }

    private void insertTransaction(final String transactionId) {
        myService.getTransactionById(transactionId).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if(response.isSuccessful()){
                    Transaction transaction = response.body();
                    eamount.setText(transaction.getAmount());
                    enote.setText(transaction.getNote());
                    ecategory.setText(transaction.getCategory());
                    edate.setText(transaction.getDate());

                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                    String inputText = transaction.getDate();
                    try {
                        Date dateTemp = inputFormat.parse(inputText);
                        String outputText = outputFormat.format(dateTemp);
                        edate.setText(outputText);
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }

                    tv_type.setText(transaction.getType());

                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
