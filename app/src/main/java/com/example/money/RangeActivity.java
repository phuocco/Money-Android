package com.example.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Adapter.HomeAdapter;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Range;
import com.example.money.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RangeActivity extends AppCompatActivity {
    HomeAdapter homeAdapter;
    private RecyclerView myRecyclerView;
    RelativeLayout range_layout;
    private MyService myService;
    RetrofitClient retrofitClient;
    boolean isDark =  false;
    TextView textView5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_range);
        textView5= findViewById(R.id.textView5);
        String startTime = "1572843600000";
        String endTime =   "1575003600000";
        myRecyclerView = (RecyclerView) findViewById(R.id.rangelayout);
        getTransactionByrange(startTime,endTime);
    }

    private void getTransactionByrange(String startTime, String endTime) {

        SharedPreferences sharedPreferences =  getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        com.example.money.models.Range range =  new Range(email,startTime,endTime);
        Call<List<Transaction>> call = myService.getTransactionByRange(range);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()){
                    final List<Transaction> transactionList = response.body();
                    Log.d("test",response.toString());

                    HomeAdapter homeAdapter = new HomeAdapter(transactionList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RangeActivity.this);
                    myRecyclerView.setLayoutManager(layoutManager);
                    myRecyclerView.setAdapter(homeAdapter);
                    homeAdapter.notifyDataSetChanged();
                    textView5.setText(response.toString());
                    Toast.makeText(RangeActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

            }
        });
    }
}
