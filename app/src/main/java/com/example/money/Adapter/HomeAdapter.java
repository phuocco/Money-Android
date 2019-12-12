package com.example.money.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.Constants;
import com.example.money.R;
import com.example.money.Transaction.DetailTransactionActivity;
import com.example.money.models.Transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder>{
    private Context mContext;
    private List<Transaction> transactionList;

    public HomeAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.oneitem_tran, parent, false);
        mContext = parent.getContext();
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.mCardTransaction.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.mCardTransaction.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        if (holder.isUSD){
            String finalString = Float.toString(Float.parseFloat(transactionList.get(position).getAmount())/holder.rate).substring(0,3);
            holder.mHomeAmount.setText(mContext.getResources().getString(R.string.currency_usd,finalString));
        }
        else {
            holder.mHomeAmount.setText(mContext.getResources().getString(R.string.currency_vnd,transactionList.get(position).getAmount()));
        }

        if(holder.isDark){
            if("Expense".equals(transactionList.get(position).getType())){
                holder.mHomeAmount.setTextColor(Color.RED);
                holder.mCardTransaction.setBackgroundResource(R.drawable.card_expense_dark);
            } else {
                holder.mHomeAmount.setTextColor(Color.GREEN);
                holder.mCardTransaction.setBackgroundResource(R.drawable.card_income_dark);
            }
            holder.mHomeCategory.setTextColor(Color.rgb(255,255,255));
        } else {
            holder.mHomeCategory.setTextColor(Color.rgb(103,58,183));
            if("Expense".equals(transactionList.get(position).getType())){
                holder.mHomeAmount.setTextColor(Color.RED);
                holder.mCardTransaction.setBackgroundResource(R.drawable.card_expense_light);
            } else {
                holder.mHomeAmount.setTextColor(Color.BLUE);
                holder.mCardTransaction.setBackgroundResource(R.drawable.card_income_light);
            }
        }
        holder.mHomeCategory.setText(transactionList.get(position).getCategory());
        holder.mHomeNote.setText(transactionList.get(position).getNote());
        //ic_date
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        String inputText = transactionList.get(position).getDate();
        try {
            Date date = inputFormat.parse(inputText);
            String outputText = outputFormat.format(date);
            holder.mHomeDate.setText(outputText);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mHomeCategory, mHomeAmount, mHomeNote, mHomeDate;
        LinearLayout mCardTransaction;
        boolean isDark,isUSD;
        float rate;

        CustomViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mCardTransaction = itemView.findViewById(R.id.card_transaction);
            mHomeCategory = itemView.findViewById(R.id.home_category);
            mHomeAmount = itemView.findViewById(R.id.home_amount);
            mHomeDate = itemView.findViewById(R.id.home_date);
            mHomeNote = itemView.findViewById(R.id.home_note);
            // SharedPreferences
            SharedPreferences preferences = itemView.getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
            isDark = preferences.getBoolean(Constants.ISDARK,false);
            isUSD = preferences.getBoolean(Constants.ISUSD,false);
            rate = preferences.getFloat(Constants.RATE,0f);

        }


        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Transaction transaction = transactionList.get(getLayoutPosition());
            Intent a = new Intent(context,DetailTransactionActivity.class);
            a.putExtra("TransactionID",transaction.getId());
            context.startActivity(a);
        }
    }

}
