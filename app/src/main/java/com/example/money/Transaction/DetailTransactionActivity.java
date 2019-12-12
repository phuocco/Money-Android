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
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.Home.HomeActivity;
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
    TextView mAmount, mCategory, mNote, mDate;

    ImageView photo;
    MyService myService;
    private FloatingActionButton mFabMain, mFabEdit, mFabShare, mFabDelete;
    private Animation mFabOpen, mFabClose, mFabClock, mFabAntiClock;
    TextView mTextViewEdit, mTextViewShare, mTextViewDelete;
    boolean isOpen = false;
    private boolean isDark =  false;

    //TODO dark mode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);
        //init
       init();
        isDark = getThemeStatePref();
        if (isDark){
            getWindow().getDecorView().setBackgroundResource(R.drawable.gradient_main_dark);
            mAmount.setTextColor(getResources().getColor(R.color.textDark));
            mCategory.setTextColor(getResources().getColor(R.color.textDark));
            mNote.setTextColor(getResources().getColor(R.color.textDark));
            mDate.setTextColor(getResources().getColor(R.color.textDark));

        }
        //back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getDetail();
        fab();
    }
    // initialize
    private void init() {
        mAmount = findViewById(R.id.detail_amount);
        mCategory = findViewById(R.id.detail_category);
        mNote = findViewById(R.id.detail_note);
        mDate = findViewById(R.id.detail_date);
        photo = findViewById(R.id.detail_photo);
    }
    //init fab & action
    private void fab() {
        mFabMain = findViewById(R.id.fab);
        mFabEdit = findViewById(R.id.fab1);
        mFabShare = findViewById(R.id.fab2);
        mFabDelete = findViewById(R.id.fab3);
        mFabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        mFabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        mFabClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        mFabAntiClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        mTextViewEdit =  findViewById(R.id.textview_edit);
        mTextViewShare = findViewById(R.id.textview_share);
        mTextViewDelete =  findViewById(R.id.textview_delete);
        mFabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {
                    mTextViewEdit.setVisibility(View.INVISIBLE);
                    mTextViewShare.setVisibility(View.INVISIBLE);
                    mTextViewDelete.setVisibility(View.INVISIBLE);
                    mFabShare.startAnimation(mFabClose);
                    mFabEdit.startAnimation(mFabClose);
                    mFabDelete.startAnimation(mFabClose);
                    mFabMain.startAnimation(mFabAntiClock);
                    mFabShare.setClickable(false);
                    mFabEdit.setClickable(false);
                    mFabDelete.setClickable(false);
                    isOpen = false;
                } else {
                    mTextViewEdit.setVisibility(View.VISIBLE);
                    mTextViewShare.setVisibility(View.VISIBLE);
                    mTextViewDelete.setVisibility(View.VISIBLE);
                    mFabShare.startAnimation(mFabOpen);
                    mFabEdit.startAnimation(mFabOpen);
                    mFabDelete.startAnimation(mFabOpen);
                    mFabMain.startAnimation(mFabClock);
                    mFabShare.setClickable(true);
                    mFabEdit.setClickable(true);
                    mFabDelete.setClickable(true);
                    isOpen = true;
                }

            }
        });
        mFabEdit.setOnClickListener(new View.OnClickListener() {
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
        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = mNote.getText().toString();
                String shareSub = mAmount.getText().toString();
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
            }
        });
        mFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete
                Intent intent = getIntent();
                final String transactionId = intent.getStringExtra("TransactionID");
                myService.deleteTransactionById(transactionId)
                        .enqueue(new Callback<Transaction>() {
                            @Override
                            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                                Toast.makeText(DetailTransactionActivity.this, "Success", Toast.LENGTH_SHORT).show();
                              //  finishAffinity();
                                startActivity(new Intent(DetailTransactionActivity.this, HomeActivity.class));
                            }

                            @Override
                            public void onFailure(Call<Transaction> call, Throwable t) {
                                Toast.makeText(DetailTransactionActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }



    private void getDetail() {
        //get id
        Intent intent = getIntent();
        final String transactionId = intent.getStringExtra("TransactionID");
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

                mAmount.setText(getString(R.string.detail_amount,transaction.getAmount()));
                mCategory.setText(getString(R.string.detail_category,transaction.getCategory()));
                if(transaction.getNote()==null){
                    mNote.setText("");
                } else {
                    mNote.setText(getString(R.string.detail_note,transaction.getNote()));
                }
                //ic_date format
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                String inputText = transaction.getDate();
                try {
                    Date dateTemp = inputFormat.parse(inputText);
                    Log.d("input",dateTemp.toString());
                    String outputText = outputFormat.format(dateTemp);
                    mDate.setText(getString(R.string.detail_date,outputText));
                } catch (ParseException ex) {
                    Log.v("Exception", ex.getLocalizedMessage());
                }

                if(transaction.getPhoto()!=null){
                    Picasso.get().load(Uri.parse(transaction.getPhoto())).into(photo);
                }
// for place holder
//                if(transaction.getPhoto()==null){
//                   Picasso.get().load(R.drawable.placeholder).into(photo);
//                } else {
//                    Picasso.get().load(Uri.parse(transaction.getPhoto())).placeholder(R.drawable.placeholder).into(photo);
//                }
            }
            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Toast.makeText(DetailTransactionActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean getThemeStatePref(){
        SharedPreferences preferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        isDark = preferences.getBoolean(Constants.ISDARK,false);
        return isDark;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
