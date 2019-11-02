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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.Constants;
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

import static android.content.Context.MODE_PRIVATE;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder>{
    Transaction transaction;
    Context mContext;
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


        //date
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        String inputText = transactionList.get(position).getDate();
        try {
            Date date = inputFormat.parse(inputText);
            String outputText = outputFormat.format(date);
            holder.home_date.setText(outputText);
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }

       // holder.home_date.setText(transactionList.get(position).getDate());

        holder.card_transaction.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_scale_animation));
        holder.home_amount.setText(transactionList.get(position).getAmount()+" $");

//        if("Expense".equals(transactionList.get(position).getType())){
//            holder.home_amount.setTextColor(Color.RED);
//            if (holder.isDark) {
//                holder.card_transaction.setBackgroundResource(R.drawable.card_expense_light);
//            } else {
//                holder.card_transaction.setBackgroundResource(R.drawable.card_bg_dark);
//            }
//        } else {
//            holder.home_amount.setTextColor(Color.GREEN);
//            if (holder.isDark) {
//                holder.card_transaction.setBackgroundResource(R.drawable.card_income_light);
//            } else {
//                holder.card_transaction.setBackgroundResource(R.drawable.card_bg);
//            }
//        }

        if(holder.isDark){
            if("Expense".equals(transactionList.get(position).getType())){
                holder.home_amount.setTextColor(Color.RED);
                holder.card_transaction.setBackgroundResource(R.drawable.card_expense_light);

            } else {
                holder.home_amount.setTextColor(Color.GREEN);
                holder.card_transaction.setBackgroundResource(R.drawable.card_income_light);
            }
        } else {

            if("Expense".equals(transactionList.get(position).getType())){
                holder.home_amount.setTextColor(Color.BLUE);
                holder.card_transaction.setBackgroundResource(R.drawable.card_bg_dark);

            } else {
                holder.home_amount.setTextColor(Color.YELLOW);
                holder.card_transaction.setBackgroundResource(R.drawable.card_bg);
            }
        }
        holder.home_category.setText(transactionList.get(position).getCategory());
        holder.home_note.setText(transactionList.get(position).getNote());
//        holder.home_event.setText(transactionList.get(position).getEvent());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView home_category,home_amount, home_note,home_date,home_event;
        LinearLayout card_transaction;
        boolean isDark;

        CustomViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card_transaction = itemView.findViewById(R.id.card_transaction);
            home_category = itemView.findViewById(R.id.home_category);
            home_amount = itemView.findViewById(R.id.home_amount);
            home_date = itemView.findViewById(R.id.home_date);
            home_note = itemView.findViewById(R.id.home_note);
//            home_event = itemView.findViewById(R.id.home_event);
            SharedPreferences preferences = itemView.getContext().getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
            isDark = preferences.getBoolean(Constants.ISDARK,false);
//            if(isDark){
//               // setDarkTheme();
//            }
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
