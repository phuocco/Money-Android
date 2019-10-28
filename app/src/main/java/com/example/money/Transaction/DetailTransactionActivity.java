package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton fab_main, fab1_mail, fab2_share;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_mail, textview_share;
    Context context;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);



        //init
       init();
        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getDetail();
        fab();
    }

    private void fab() {
        fab_main = findViewById(R.id.fab);
        fab1_mail = findViewById(R.id.fab1);
        fab2_share = findViewById(R.id.fab2);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_mail = (TextView) findViewById(R.id.textview_mail);
        textview_share = (TextView) findViewById(R.id.textview_share);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {

                    textview_mail.setVisibility(View.INVISIBLE);
                    textview_share.setVisibility(View.INVISIBLE);
                    fab2_share.startAnimation(fab_close);
                    fab1_mail.startAnimation(fab_close);
                    fab_main.startAnimation(fab_anticlock);
                    fab2_share.setClickable(false);
                    fab1_mail.setClickable(false);
                    isOpen = false;
                } else {
                    textview_mail.setVisibility(View.VISIBLE);
                    textview_share.setVisibility(View.VISIBLE);
                    fab2_share.startAnimation(fab_open);
                    fab1_mail.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fab2_share.setClickable(true);
                    fab1_mail.setClickable(true);
                    isOpen = true;
                }

            }
        });
        fab1_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String transactionId = intent.getStringExtra("TransactionID");
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.ID,transactionId);
                editor.apply();
                startActivity(new Intent(DetailTransactionActivity.this,EditTransactionActivity.class));

            }
        });
    }

    private void getDetail() {
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
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.ID,transactionId);
                editor.apply();

                // Toast.makeText(DetailTransactionActivity.this, ""+transaction.getId(), Toast.LENGTH_SHORT).show();
                //id.setText(transaction.getId());
                amount.setText(transaction.getAmount());
                category.setText(transaction.getCategory());
                id.setText(transaction.getEmail());
                if(transaction.getNote()==null){
                    note.setText("");
                } else {
                    note.setText(transaction.getNote());
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

    private void init() {
        id = findViewById(R.id.detail_id);
        amount = findViewById(R.id.detail_amount);
        category = findViewById(R.id.detail_category);
        note = findViewById(R.id.detail_note);
        date = findViewById(R.id.detail_date);
        remind = findViewById(R.id.detail_remind);
        photo = findViewById(R.id.detail_photo);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
