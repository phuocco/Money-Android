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
import com.example.money.models.Range;
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
public class RangeFragment extends Fragment {
    private RecyclerView myRecyclerView;
    private RelativeLayout mRangeLayout;
    private MyService myService;
    private boolean isDark =  false;

    public RangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_range, container, false);
        mRangeLayout = view.findViewById(R.id.range_layout);

        String startTime =  getArguments().getString("startDate");
        String endTime =  getArguments().getString("endDate");
//        String startTime = "1572843600000";
//        String endTime =   "1575003600000";
        Log.d("test",startTime+ " "+ endTime);
        isDark = getThemeStatePref();
        if (isDark){
            mRangeLayout.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            mRangeLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }
        myRecyclerView = (RecyclerView) view.findViewById(R.id.rv_range);
        getTransactionByRange(startTime,endTime);
        return view;
    }

    private void getTransactionByRange(String startTime,String endTime) {
        SharedPreferences sharedPreferences =  getContext().getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.EMAIL,null).replace("\"", "");
        Retrofit retrofitClient = RetrofitClient.getInstance();
        myService = retrofitClient.create(MyService.class);
        Range range =  new Range(email,startTime,endTime);
        Call<List<Transaction>> call = myService.getTransactionByRange(range);
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
