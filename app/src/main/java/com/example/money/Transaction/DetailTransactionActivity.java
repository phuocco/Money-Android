package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailTransactionActivity extends AppCompatActivity {
    TextView id,amount,category,note,date,remind;
    ImageView photo;
    MyService myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        //init
        id = findViewById(R.id.detail_id);
        amount = findViewById(R.id.detail_amount);
        category = findViewById(R.id.detail_category);
        note = findViewById(R.id.detail_note);
        date = findViewById(R.id.detail_date);
        remind = findViewById(R.id.detail_remind);
        photo = findViewById(R.id.detail_photo);

        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get id
        Intent intent = getIntent();
        final String transactionId = intent.getStringExtra("TransactionID");
       // Toast.makeText(this, ""+transactionId, Toast.LENGTH_SHORT).show();

        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        Call<Transaction> call = myService.getTransactionById(transactionId);
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                Transaction transaction = response.body();
               // Toast.makeText(DetailTransactionActivity.this, ""+transaction.getId(), Toast.LENGTH_SHORT).show();
                //id.setText(transaction.getId());
                amount.setText("Amount: "+transaction.getAmount());
                category.setText("Category: "+transaction.getCategory());

                if(transaction.getNote()==null){
                    note.setText("Note: ");
                } else {
                    note.setText("Note: "+transaction.getNote());

                }

                //date format
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                String inputText = transaction.getDate();
                try {
                    Date dateTemp = inputFormat.parse(inputText);
                    String outputText = outputFormat.format(dateTemp);
                    date.setText(outputText);
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }

                if(transaction.getPhoto()!=null){
                    Picasso.get().load(Uri.parse(transaction.getPhoto())).into(photo);
                }
                // remind.setText(transaction.getPhoto());
//                if(transaction.getPhoto()==null){
//                   Picasso.get().load(R.drawable.placeholder).into(photo);
//                } else {
//                    Picasso.get().load(Uri.parse(transaction.getPhoto())).placeholder(R.drawable.placeholder).into(photo);
//                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Toast.makeText(DetailTransactionActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
