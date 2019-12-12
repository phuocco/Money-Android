package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
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
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditTransactionActivity extends AppCompatActivity {
    TextInputEditText mEditTextAmount, mEditTextNote;
    TextInputLayout mLayoutAmount, mLayoutNote;

    MyService myService;
    MaterialButton mButtonEdit;
    TextView mTextViewType, mTextViewDate;
    Spinner mSpnCategory;
    String mStrCategory;
    private boolean isDark =  false;

    //TODO dark mode
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        mEditTextAmount = findViewById(R.id.edit_amount);
        mEditTextNote = findViewById(R.id.edit_note);
        mSpnCategory =  findViewById(R.id.edit_category);
        mTextViewDate = findViewById(R.id.edit_date);
        mTextViewType =  findViewById(R.id.edit_type);
        mLayoutAmount = findViewById(R.id.layout_edit_amount);
        mLayoutNote = findViewById(R.id.layout_edit_note);
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        final String transactionId = sharedPreferences.getString(Constants.ID,"").replace("\"", "");
        isDark = getThemeStatePref();

        if (isDark){
            getWindow().getDecorView().setBackgroundResource(R.drawable.gradient_dark_income);
            mEditTextAmount.setTextColor(getResources().getColor(R.color.textDark));
            mEditTextNote.setTextColor(getResources().getColor(R.color.textDark));
            mTextViewDate.setTextColor(getResources().getColor(R.color.textDark));
            mLayoutAmount.setDefaultHintTextColor(getResources().getColorStateList(R.color.textDark));
            mLayoutNote.setDefaultHintTextColor(getResources().getColorStateList(R.color.textDark));

        } else {
            getWindow().getDecorView().setBackgroundResource(R.drawable.gradient_main);
        }

        mSpnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStrCategory = mSpnCategory.getSelectedItem().toString();
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        mButtonEdit = findViewById(R.id.button_edit);
        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount =  mEditTextAmount.getText().toString();
                String note =  mEditTextNote.getText().toString();
                String date =  mTextViewDate.getText().toString();
                String category = mStrCategory;
                String type = mTextViewType.getText().toString();
                updateTrans(transactionId,amount,category,type,note,date);
                startActivity(new Intent(EditTransactionActivity.this, HomeActivity.class));
            }
        });

        insertTransaction(transactionId);

    }

    private void pickDate() {
        final Calendar calendar = Calendar.getInstance();
        int ngay =  calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i,i1,i2);
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM-dd",Locale.US);
                String date2 = simpleDateFormat.format(calendar.getTime());
                //  String result= getString(R.string.selected_date,ic_date);
                mTextViewDate.setText(getString(R.string.edit_date, date2));
            }
        },nam,thang,ngay+1);
        datePickerDialog.show();
    }

    private void updateTrans(String transactionId, String amount,String category, String type,String note, String date) {
        myService.updateTransaction(transactionId,amount,category,type,note,date)
                .enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                      if(response.isSuccessful())
                      {
                          Toast.makeText(EditTransactionActivity.this, "Edited", Toast.LENGTH_SHORT).show();
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
                    mEditTextAmount.setText(transaction.getAmount());
                    mEditTextNote.setText(transaction.getNote());
                    mTextViewDate.setText(transaction.getDate());

                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    String inputText = transaction.getDate();
                    try {
                        Date dateTemp = inputFormat.parse(inputText);
                        String outputText = outputFormat.format(dateTemp);
                        mTextViewDate.setText(getString(R.string.edit_date,outputText));
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }
                    String compareValue = transaction.getCategory();
                    //list
                    AddExpenseActivity listExpense =  new AddExpenseActivity();
                    AddIncomeActivity listIncome = new AddIncomeActivity();
                    List<String> list;
                    if("Expense".equals(transaction.getType())){
                        list = listExpense.getExpenseList();
                    } else {
                        list = listIncome.getIncomeList();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    mSpnCategory.setAdapter(adapter);
                    if (compareValue != null) {
                        int spinnerPosition = adapter.getPosition(compareValue);
                        mSpnCategory.setSelection(spinnerPosition);
                    }
                    mTextViewType.setText(transaction.getType());

                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {

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
