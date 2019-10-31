package com.example.money.Home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.money.Adapter.HomeAdapter;
import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.Transaction.AddExpenseActivity;
import com.example.money.Transaction.AddIncomeActivity;
import com.example.money.models.Transaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.Colors;

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
    TextView title;
    //fab
    private FloatingActionButton fab_home, fab_add_ex, fab_add_in;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView tv_add_ex, tv_add_in;
    Boolean isOpen = false;

    LinearLayout card;


    public ThisMonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_this_month, container, false);
        this_month_layout = view.findViewById(R.id.this_month_layout);
        title = view.findViewById(R.id.action_bar_title);
        card = (LinearLayout) view.findViewById(R.id.card_transaction);
        sum_ex = view.findViewById(R.id.this_month_total_ex);
        sum_in = view.findViewById(R.id.this_month_total_in);
        sum_all = view.findViewById(R.id.this_month_total_all);
        myRecyclerView = view.findViewById(R.id.rv_thismonth);
        //fab
        fab_home = view.findViewById(R.id.fab_home);
        fab_add_ex = view.findViewById(R.id.fab_add_ex);
        fab_add_in = view.findViewById(R.id.fab_add_in);
        fab_close = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_rotate_anticlock);
        tv_add_ex = (TextView) view.findViewById(R.id.tv_add_ex);
        tv_add_in = (TextView) view.findViewById(R.id.tv_add_in);


        fab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {

                    tv_add_ex.setVisibility(View.INVISIBLE);
                    tv_add_in.setVisibility(View.INVISIBLE);
                    tv_add_ex.setAnimation(fab_close);
                    tv_add_in.setAnimation(fab_close);
                    fab_add_in.startAnimation(fab_close);
                    fab_add_ex.startAnimation(fab_close);
                    fab_home.startAnimation(fab_anticlock);
                    fab_add_in.setClickable(false);
                    fab_add_ex.setClickable(false);
                    isOpen = false;
                } else {
                    tv_add_ex.setVisibility(View.VISIBLE);
                    tv_add_in.setVisibility(View.VISIBLE);
                    fab_add_in.startAnimation(fab_open);
                    fab_add_ex.startAnimation(fab_open);
                    tv_add_ex.setAnimation(fab_open);
                    tv_add_in.setAnimation(fab_open);
                    fab_home.startAnimation(fab_clock);
                    fab_add_in.setClickable(true);
                    fab_add_ex.setClickable(true);
                    isOpen = true;
                }

            }
        });
        fab_add_ex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddExpenseActivity.class));
            }
        });
        fab_add_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddIncomeActivity.class));

            }
        });



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
                    HomeAdapter homeAdapter = new HomeAdapter(transactionList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    myRecyclerView.setLayoutManager(layoutManager);
                    homeAdapter.notifyDataSetChanged();
                    myRecyclerView.setAdapter(homeAdapter);
                  //  Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                    Log.d("test2", transactionList.toString());

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
                    sum_in.setText(String.valueOf(in));
                    sum_all.setText(String.valueOf(sum));

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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        // Refresh tab data:

        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }
}





