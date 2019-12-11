package com.example.money.Transaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.MainActivity;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddIncomeActivity extends AppCompatActivity {
    TextInputEditText mEditTextAmount, mEditTextNote;
    TextInputLayout mLayoutAmount, mLayoutNote;
    CardView mButtonAddIncome;
    MyService myService;
    ImageView mImageView;
    TextView mTextViewAddDate;
    String mStrCategory;
    Spinner mSpnCategory;
    EditText mEditTextType;
    private boolean isDark =  false;

    //firebase
    FirebaseStorage mStorage;
    StorageReference mStorageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        init();
        isDark = getThemeStatePref();

        mLayoutAmount = findViewById(R.id.layout_add_amount_in);
        mLayoutNote = findViewById(R.id.layout_add_note_in);
        List<String> list = new ArrayList<>();
        list.add("Salary");
        list.add("Gift");
        list.add("Loan");

        if (isDark){
            getWindow().getDecorView().setBackgroundResource(R.drawable.gradient_dark_income);
            mEditTextAmount.setTextColor(getResources().getColor(R.color.textDark));
            mLayoutAmount.setDefaultHintTextColor(getResources().getColorStateList(R.color.textDark));
            mEditTextNote.setTextColor(getResources().getColor(R.color.textDark));
            mLayoutNote.setDefaultHintTextColor(getResources().getColorStateList(R.color.textDark));
            mTextViewAddDate.setTextColor(getResources().getColor(R.color.textDark));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpnCategory.setAdapter(adapter);
        mSpnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                mStrCategory = mSpnCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        mButtonAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               uploadImage();
            }
        });

    }

    private void init(){
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mImageView = findViewById(R.id.image_add_income);

        mEditTextAmount =findViewById(R.id.add_in_amount);
        mEditTextNote =findViewById(R.id.add_in_note);
        mSpnCategory =findViewById(R.id.add_in_category);
        mEditTextType =findViewById(R.id.add_in_type);
        mButtonAddIncome = findViewById(R.id.button_add_in);
        mTextViewAddDate =findViewById(R.id.add_in_date);
        mTextViewAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });
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
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String date = simpleDateFormat.format(calendar.getTime());
                //  String result= getString(R.string.selected_date,ic_date);
                mTextViewAddDate.setText(getString(R.string.selected_date, date));
            }
        },nam,thang,ngay);
        datePickerDialog.show();
    }

    //choose image
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture"),PICK_IMAGE_REQUEST);
    }
    //activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap =  MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    //upload image
    private void uploadImage() {
        if(filePath == null){
            SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
            String email = sharedPreferences.getString(Constants.EMAIL,"");
            email = email.replace("\"", "");
            boolean isUSD =  sharedPreferences.getBoolean(Constants.ISUSD,false);
            float rate = sharedPreferences.getFloat(Constants.RATE,0f);
            email = email.replace("\"", "");
            String amount;
            if(isUSD){
                String textAmount = mEditTextAmount.getText().toString();
                float amountFloat = Float.parseFloat(textAmount);
                float finalAmountFloat = amountFloat * rate;
                amount = String.valueOf(finalAmountFloat);
            } else {
                amount = mEditTextAmount.getText().toString();
            }
            String note = mEditTextNote.getText().toString();
            String category = mStrCategory;
            String type = mEditTextType.getText().toString();
            String date = mTextViewAddDate.getText().toString();
            String photo = "";
            addTransaction(email,amount,category,type,note,date,photo);
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("uploading");
            progressDialog.show();
            final   StorageReference ref = mStorageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    // String email = ed_email.getText().toString();
                                    //shared email
                                    SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
                                    String email = sharedPreferences.getString(Constants.EMAIL,"");
                                    email = email.replace("\"", "");
                                    boolean isUSD =  sharedPreferences.getBoolean(Constants.ISUSD,false);
                                    float rate = sharedPreferences.getFloat(Constants.RATE,0f);
                                    email = email.replace("\"", "");
                                    String amount;
                                    if(isUSD){
                                        String textAmount = mEditTextAmount.getText().toString();
                                        float amountFloat = Float.parseFloat(textAmount);
                                        float finalAmountFloat = amountFloat * rate;
                                        amount = String.valueOf(finalAmountFloat);
                                    } else {
                                        amount = mEditTextAmount.getText().toString();
                                    }
                                    String note = mEditTextNote.getText().toString();
                                    String category = mStrCategory;
                                    String type = "Income";
                                    String date = mTextViewAddDate.getText().toString();
                                    String photo = uri.toString();
                                    //   Toast.makeText(AddExpenseActivity.this, ""+photo, Toast.LENGTH_SHORT).show();
                                    addTransaction(email,amount,category,type,note,date,photo);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddIncomeActivity.this, "failllllll", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void addTransaction(String email, String amount,String category,String type, String note,String date,  String photo) {
        myService.addTransaction(email,amount,category,type,note,date,photo)
                .enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(AddIncomeActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddIncomeActivity.this, MainActivity.class));

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
}
