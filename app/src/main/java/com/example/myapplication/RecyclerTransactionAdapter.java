package com.example.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerTransactionAdapter extends RecyclerView.Adapter<RecyclerTransactionAdapter.ViewHolder> {
    Context context;
    ArrayList<Transaction> transactions;

    RecyclerTransactionAdapter(Context context, ArrayList<Transaction> transactions){
        this.context = context;
        this.transactions = transactions;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.transaction_card,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(transactions.get(position).transaction_title);
        holder.amount.setText("Rs "+transactions.get(position).amount);
        holder.note.setText(transactions.get(position).note);

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, amount,note;

        public ViewHolder(View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.payment_title);
            amount = itemView.findViewById(R.id.payment_amount);
            note = itemView.findViewById(R.id.note_view);


        }

    }

}












