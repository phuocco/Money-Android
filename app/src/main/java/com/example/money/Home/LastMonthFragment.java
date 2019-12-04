package com.example.money.Home;



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
import android.widget.Toast;

import com.example.money.Adapter.HomeAdapter;
import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Retrofit.MyService;
import com.example.money.Retrofit.RetrofitClient;
import com.example.money.models.Transaction;


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
    private RecyclerView myRecyclerView;
    private RelativeLayout last_month_layout;
    private MyService myService;
    private boolean isDark =  false;
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
        myRecyclerView =  view.findViewById(R.id.rv_lastmonth);
        return view;
    }
    // get transaction by month
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
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean getThemeStatePref(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        isDark = preferences.getBoolean(Constants.ISDARK,false);
        return isDark;
    }
}





