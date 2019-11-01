package com.example.money.Home;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Adapter.HomeAdapter;
import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastMonthFragment extends Fragment {
    HomeAdapter homeAdapter;
    private RecyclerView myRecyclerView;
    RelativeLayout last_month_layout;
    private MyService myService;
    RetrofitClient retrofitClient;
    boolean isDark =  false;
    public LastMonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_last_month, container, false);
        last_month_layout = view.findViewById(R.id.last_month_layout);

        isDark = getThemeStatePref();
        if (isDark){
            last_month_layout.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            last_month_layout.setBackgroundColor(getResources().getColor(R.color.white));
        }

        final Calendar calendar = Calendar.getInstance();
        final  int month = calendar.get(Calendar.MONTH);
        final  int year = calendar.get(Calendar.YEAR);

        getTransactionByMonth( String.valueOf(month), String.valueOf(year));
        myRecyclerView = (RecyclerView) view.findViewById(R.id.rv_lastmonth);

        return view;
    }

    private void getTransactionByMonth(String month, String year) {
        SharedPreferences sharedPreferences =  getContext().getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        Transaction transaction =  new Transaction(email,month,year);
        Retrofit retrofitClient = RetrofitClient.getInstance();

        myService = retrofitClient.create(MyService.class);
        Call<List<Transaction>> call = myService.getAllTransactionsByEmail(transaction);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()){
                    final  List<Transaction> transactionList = response.body();
                    Log.d("test",response.toString());
                    Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                    HomeAdapter homeAdapter = new HomeAdapter(transactionList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    myRecyclerView.setLayoutManager(layoutManager);
                    myRecyclerView.setAdapter(homeAdapter);
                    homeAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

            }
        });
    }

    private boolean getThemeStatePref(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        boolean isDark = preferences.getBoolean(Constants.ISDARK,false);
        return isDark;
    }
}





