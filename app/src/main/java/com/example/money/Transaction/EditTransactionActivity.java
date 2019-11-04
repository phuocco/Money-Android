package com.example.money.Transaction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
    TextInputEditText eamount,enote;
    MyService myService;
    MaterialButton button_edit;
    TextView tv_type,edate;
    Spinner spn_ecategory;
    String ecategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);
        eamount = findViewById(R.id.edit_amount);
        enote = findViewById(R.id.edit_note);
        spn_ecategory =  findViewById(R.id.edit_category);
        edate = findViewById(R.id.edit_date);
        tv_type =  findViewById(R.id.edit_type);
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        final String transactionId = sharedPreferences.getString(Constants.ID,"").replace("\"", "");
        //final String transactionId = "5dac689c788f27175c88b5f4";

        spn_ecategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ecategory = spn_ecategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        button_edit = findViewById(R.id.button_edit);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount =  eamount.getText().toString();
                String note =  enote.getText().toString();
                String date =  edate.getText().toString();
                String category =  ecategory;
                String type = tv_type.getText().toString();
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
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM-dd");
                String date2 = simpleDateFormat.format(calendar.getTime());
                //  String result= getString(R.string.selected_date,ic_date);
                edate.setText(getString(R.string.edit_date, date2));
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
                          Toast.makeText(EditTransactionActivity.this, "ac", Toast.LENGTH_SHORT).show();
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
                    edate.setText(transaction.getDate());

                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                    String inputText = transaction.getDate();
                    try {
                        Date dateTemp = inputFormat.parse(inputText);
                        String outputText = outputFormat.format(dateTemp);
                        edate.setText(getString(R.string.edit_date,outputText));
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }
                    String compareValue = transaction.getCategory();

                    //list
                    List<String> list_ex = new ArrayList<>();
                    list_ex.add("Food");
                    list_ex.add("Water");
                    list_ex.add("Entertainment");

                    List<String> list_in = new ArrayList<>();
                    list_in.add("Salary");
                    list_in.add("Gift");
                    list_in.add("Loan");

                    List<String> list = new ArrayList<>();
                    if("Expense".equals(transaction.getType())){
                        list = list_ex;
                    } else {
                        list = list_in;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,list);
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                    spn_ecategory.setAdapter(adapter);
                    if (compareValue != null) {
                        int spinnerPosition = adapter.getPosition(compareValue);
                        spn_ecategory.setSelection(spinnerPosition);
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
