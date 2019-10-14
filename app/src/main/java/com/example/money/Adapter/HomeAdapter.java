package com.example.money.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.money.R;
import com.example.money.Transaction.DetailTransactionActivity;
import com.example.money.models.Transaction;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.CustomViewHolder>{
    Transaction transaction;
    private List<Transaction> transactionList;

    public HomeAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;

    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.oneitem_tran, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.home_amount.setText(transactionList.get(position).getAmount());
        holder.home_category.setText(transactionList.get(position).getCategory());
        holder.home_note.setText(transactionList.get(position).getNote());
        holder.home_date.setText(transactionList.get(position).getDate());
        holder.home_event.setText(transactionList.get(position).getEvent());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView home_category,home_amount, home_note,home_date,home_event;
        CustomViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            home_category = itemView.findViewById(R.id.home_category);
            home_amount = itemView.findViewById(R.id.home_amount);
            home_note = itemView.findViewById(R.id.home_note);
            home_date = itemView.findViewById(R.id.home_date);
            home_event = itemView.findViewById(R.id.home_event);
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
