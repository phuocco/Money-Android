package com.example.money.Home;


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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Adapter.HomeAdapter;
import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
public class ThisMonthFragment extends Fragment {
    HomeAdapter homeAdapter;
    private RecyclerView myRecyclerView;
    FloatingActionButton floatingActionButton;
    RelativeLayout this_month_layout;
    private MyService myService;
    RetrofitClient retrofitClient;
    boolean isDark =  false;
    TextView sum_ex,sum_in,sum_all;
    public ThisMonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_this_month, container, false);
        floatingActionButton = view.findViewById(R.id.icon_fab);
        this_month_layout = view.findViewById(R.id.this_month_layout);
        sum_ex = view.findViewById(R.id.this_month_total_ex);
        sum_in = view.findViewById(R.id.this_month_total_in);
        sum_all = view.findViewById(R.id.this_month_total_all);
        myRecyclerView = view.findViewById(R.id.rv_thismonth);

        //retrofit
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        isDark = getThemeStatePref();
        if (isDark){
            this_month_layout.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            this_month_layout.setBackgroundColor(getResources().getColor(R.color.white));
        }
        //get email
        SharedPreferences sharedPreferences =  getContext().getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");



        //get all by email
        final Transaction transaction =  new Transaction(email);
        Call<List<Transaction>> call = myService.getAllTransactions(transaction);
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()){
                    final List<Transaction> transactionList = response.body();

                    HomeAdapter homeAdapter = new HomeAdapter(transactionList,isDark);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    myRecyclerView.setLayoutManager(layoutManager);
                    myRecyclerView.setAdapter(homeAdapter);
                  //  Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();

                    int sum =0;
                    int ex = 0;
                    int in = 0;
                    for (Transaction transaction1: response.body()){
                        if("Expense".equals(transaction1.getType()))
                        {
                            ex = ex + Integer.parseInt(transaction1.getAmount());
                        }
                        if("Income".equals(transaction1.getType()))
                        {
                            in = in + Integer.parseInt(transaction1.getAmount());
                        }
                        sum = sum + Integer.parseInt(transaction1.getAmount());
                    }
                    sum_ex.setText(String.valueOf(ex));
                    sum_all.setText(String.valueOf(sum));
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isDark = !isDark;
                            if (isDark){
                                this_month_layout.setBackgroundColor(getResources().getColor(R.color.black));
                            } else {
                                this_month_layout.setBackgroundColor(getResources().getColor(R.color.white));
                            }
                            HomeAdapter homeAdapter = new HomeAdapter(transactionList,isDark);
                            myRecyclerView.setAdapter(homeAdapter);
                            saveThemeStatePref(isDark);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

            }
        });
        return view;
    }

    private void saveThemeStatePref(boolean isDark) {
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isDark",isDark);
        editor.apply();
    }
    private boolean getThemeStatePref(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        boolean isDark = preferences.getBoolean(Constants.ISDARK,false);
        return isDark;
    }
}





