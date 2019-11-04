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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Constants;
import com.example.money.Home.TransactionCategoryActivity;
import com.example.money.MainActivity;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddExpenseActivity extends AppCompatActivity {
    TextInputEditText ed_email,ed_amount,ed_note;
    CardView button_add_ex;
    MyService myService;
    ImageView imageView;
    TextView tv_add_date;
    Spinner spn_category;
    String ed_category;
    EditText ed_type;
    //firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        init();

        List<String> list = new ArrayList<>();
        list.add("Food");
        list.add("Water");
        list.add("Entertainment");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spn_category.setAdapter(adapter);
        spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ed_category = spn_category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        button_add_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }
    private void init(){
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView = findViewById(R.id.image_add_expense);

        ed_amount =findViewById(R.id.add_ex_amount);
        ed_note =findViewById(R.id.add_ex_note);
        spn_category =findViewById(R.id.add_ex_category);
        ed_type =findViewById(R.id.add_ex_type);
        button_add_ex = findViewById(R.id.button_add_ex);
        tv_add_date = findViewById(R.id.add_ex_date);
        tv_add_date.setOnClickListener(new View.OnClickListener() {
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
                SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(calendar.getTime());
              //  String result= getString(R.string.selected_date,ic_date);
                tv_add_date.setText(getString(R.string.selected_date, date));
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
                imageView.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if(filePath == null){
            SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
            String email = sharedPreferences.getString(Constants.EMAIL,null);
            email = email.replace("\"", "");
            String amount = ed_amount.getText().toString();
            String note = ed_note.getText().toString();
            String category = ed_category;
            String type = ed_type.getText().toString();
            String date = tv_add_date.getText().toString();
            String photo = "";
            addTransaction(email,amount,category,type,note,date,photo);
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("uploading");
            progressDialog.show();
            final   StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
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
                                    String email = sharedPreferences.getString(Constants.EMAIL,null);
                                    email = email.replace("\"", "");
                                    String amount = ed_amount.getText().toString();
                                    String note = ed_note.getText().toString();
                                    String category = ed_category;
                                    String type = "Expense";
                                    String date = tv_add_date.getText().toString();
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
                            Toast.makeText(AddExpenseActivity.this, "failllllll", Toast.LENGTH_SHORT).show();
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

    private void addTransaction(String email, String amount,String category, String type,String note,String date, String photo) {
        myService.addTransaction(email,amount,category,type,note,date,photo)
                .enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(AddExpenseActivity.this, "success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AddExpenseActivity.this, MainActivity.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {
                        Toast.makeText(AddExpenseActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
