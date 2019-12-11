package com.example.money.Home;


import android.content.Intent;
import android.content.SharedPreferences;
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
    private RecyclerView myRecyclerView;
    private RelativeLayout mThisMonthLayout;
    private MyService myService;
    private boolean isDark =  false;
    private TextView mSumExpense;
    private TextView mSumIncome;
    private TextView mSumAll;
    private TextView mTitle;
    LinearLayout mSumThisMonth;
    //fab
    private FloatingActionButton mFabHome, mFabAllExpense, mFabAllIncome;
    private Animation mFabOpen, mFabClose, mFabClock, mFabAntiClock;
    private TextView mTextViewAddExpense;
    private TextView mTextViewAddIncome;
    Boolean isOpen = false;


    public ThisMonthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_this_month, container, false);
        mThisMonthLayout = view.findViewById(R.id.this_month_layout);
        mTitle = view.findViewById(R.id.action_bar_title);
        mSumExpense = view.findViewById(R.id.this_month_total_ex);
        mSumIncome = view.findViewById(R.id.this_month_total_in);
        mSumAll = view.findViewById(R.id.this_month_total_all);
        myRecyclerView = view.findViewById(R.id.rv_thismonth);
        mSumThisMonth = view.findViewById(R.id.sum_this_month);
        //fab
        mFabHome = view.findViewById(R.id.fab_home);
        mFabAllExpense = view.findViewById(R.id.fab_add_ex);
        mFabAllIncome = view.findViewById(R.id.fab_add_in);
        mFabClose = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_close);
        mFabOpen = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_open);
        mFabClock = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_rotate_clock);
        mFabAntiClock = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.fab_rotate_anticlock);
        mTextViewAddExpense = view.findViewById(R.id.tv_add_ex);
        mTextViewAddIncome = view.findViewById(R.id.tv_add_in);

        mFabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    mTextViewAddExpense.setVisibility(View.INVISIBLE);
                    mTextViewAddIncome.setVisibility(View.INVISIBLE);
                    mTextViewAddExpense.setAnimation(mFabClose);
                    mTextViewAddIncome.setAnimation(mFabClose);
                    mFabAllIncome.startAnimation(mFabClose);
                    mFabAllExpense.startAnimation(mFabClose);
                    mFabHome.startAnimation(mFabAntiClock);
                    mFabAllIncome.setClickable(false);
                    mFabAllExpense.setClickable(false);
                    isOpen = false;
                } else {
                    mTextViewAddExpense.setVisibility(View.VISIBLE);
                    mTextViewAddIncome.setVisibility(View.VISIBLE);
                    mFabAllIncome.startAnimation(mFabOpen);
                    mFabAllExpense.startAnimation(mFabOpen);
                    mTextViewAddExpense.setAnimation(mFabOpen);
                    mTextViewAddIncome.setAnimation(mFabOpen);
                    mFabHome.startAnimation(mFabClock);
                    mFabAllIncome.setClickable(true);
                    mFabAllExpense.setClickable(true);
                    isOpen = true;
                }
            }
        });
        mFabAllExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddExpenseActivity.class));
            }
        });
        mFabAllIncome.setOnClickListener(new View.OnClickListener() {
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
            mThisMonthLayout.setBackgroundColor(getResources().getColor(R.color.black));
            mSumThisMonth.setBackgroundResource(R.drawable.card_sum_dark);
        } else {
            mThisMonthLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mSumThisMonth.setBackgroundResource(R.drawable.card_sum_light);
        }
        //get email
        SharedPreferences sharedPreferences =  getContext().getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        final float rate =sharedPreferences.getFloat(Constants.RATE,0f);
        final boolean isUSD =  sharedPreferences.getBoolean(Constants.ISUSD,false);

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
                    Log.d("test2", transactionList.toString());
                    if(isUSD){
                        float sumary =0;
                        float expense = 0;
                        float income = 0;
                        float finalSum = 0,finalEx = 0,finalIn = 0;
                        for (Transaction transactionAll: response.body()){
                            if("Expense".equals(transactionAll.getType()))
                            {
                                expense = expense + Float.parseFloat(transactionAll.getAmount());
                                finalEx = expense/rate;
                            }
                            if("Income".equals(transactionAll.getType()))
                            {
                                income = income + Float.parseFloat((transactionAll.getAmount()));
                                finalIn = income /rate;
                            }
                            sumary = sumary + Float.parseFloat((transactionAll.getAmount()));
                            finalSum = sumary/rate;
                        }
                        mSumExpense.setText(getString(R.string.currency_usd,String.valueOf(finalEx)));
                        mSumIncome.setText(getString(R.string.currency_usd,String.valueOf(finalIn)));
                        mSumAll.setText(getString(R.string.currency_usd,String.valueOf(finalSum)));
                    } else {
                        int summary =0;
                        int expense = 0;
                        int income = 0;
                        for (Transaction transactionAll: response.body()){
                            if("Expense".equals(transactionAll.getType()))
                            {
                                expense = expense + (int) Double.parseDouble(transactionAll.getAmount());
                            }
                            if("Income".equals(transactionAll.getType()))
                            {
                                income = income + (int) Double.parseDouble(transactionAll.getAmount());
                            }
                            summary = summary + (int) Double.parseDouble(transactionAll.getAmount());
                        }
                        mSumExpense.setText(getString(R.string.currency_vnd,String.valueOf(expense)));
                        mSumIncome.setText(getString(R.string.currency_vnd,String.valueOf(income)));
                        mSumAll.setText(getString(R.string.currency_vnd,String.valueOf(summary)));
                    }
            }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    private boolean getThemeStatePref(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        isDark = preferences.getBoolean(Constants.ISDARK,false);
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





