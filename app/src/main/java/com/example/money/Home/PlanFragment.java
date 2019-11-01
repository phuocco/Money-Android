package com.example.money.Home;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.money.Adapter.HomeAdapter;
import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment {
    private HomeAdapter homeAdapter;
    private RecyclerView myRecyclerView;
    private MyService myService;
    RetrofitClient retrofitClient;
    boolean isDark =  false;
    FrameLayout plan_layout;
    public PlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_plan, container, false);
        myRecyclerView = view.findViewById(R.id.rv_plan);
        plan_layout = view.findViewById(R.id.plan_layout);
        isDark = getThemeStatePref();
        if (isDark){
            plan_layout.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            plan_layout.setBackgroundColor(getResources().getColor(R.color.white));
        }


        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        Call<List<Transaction>> call = myService.getAllPlanTransactions();
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful()){
                    List<Transaction> transactionList = response.body();
                    HomeAdapter homeAdapter = new HomeAdapter(transactionList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    myRecyclerView.setLayoutManager(layoutManager);
                    myRecyclerView.setAdapter(homeAdapter);
                    Toast.makeText(getActivity(), "aa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {

            }
        });

        return view;
    }
    private boolean getThemeStatePref(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        boolean isDark = preferences.getBoolean(Constants.ISDARK,false);
        return isDark;
    }
}

