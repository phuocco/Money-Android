package com.example.money.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.Home.HomeActivity;
import com.example.money.R;
import com.example.money.Transaction.DetailTransactionActivity;
import com.example.money.models.Transaction;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder>{
    Transaction transaction;
    Context mContext;
    private List<Transaction> transactionList;
    boolean isDark = false;

    public HomeAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public HomeAdapter(List<Transaction> transactionList, boolean isDark) {
        this.transactionList = transactionList;
        this.isDark = isDark;
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

        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
        String inputText = transactionList.get(position).getDate();
        try {
            Date date = inputFormat.parse(inputText);
            String outputText = outputFormat.format(date);
            holder.home_date.setText(outputText);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        holder.card_transaction.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.home_amount.setText(transactionList.get(position).getAmount()+" $");
        holder.home_category.setText(transactionList.get(position).getCategory());
//        holder.home_note.setText(transactionList.get(position).getNote());
//        holder.home_event.setText(transactionList.get(position).getEvent());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView home_category,home_amount, home_note,home_date,home_event;
        LinearLayout card_transaction;

        CustomViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card_transaction = itemView.findViewById(R.id.card_transaction);
            home_category = itemView.findViewById(R.id.home_category);
            home_amount = itemView.findViewById(R.id.home_amount);
            home_date = itemView.findViewById(R.id.home_date);
//            home_note = itemView.findViewById(R.id.home_note);
//            home_event = itemView.findViewById(R.id.home_event);

            if(isDark){
                setDarkTheme();
            }
        }
        private void setDarkTheme(){
            card_transaction.setBackgroundResource(R.drawable.card_bg_dark);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Transaction transaction = transactionList.get(getLayoutPosition());
           // Toast.makeText(context, ""+transaction.getId(), Toast.LENGTH_SHORT).show();
            Intent a = new Intent(context,DetailTransactionActivity.class);
            a.putExtra("TransactionID",transaction.getId());
            context.startActivity(a);
        }
    }
  
}
