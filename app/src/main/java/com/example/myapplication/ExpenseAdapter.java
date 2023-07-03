package com.example.myapplication;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.collection.LLRBNode;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {


    private Context context;
    private OnItemsClick onItemsClick;
    private List<ExpenseModel> expenseModelList;

    public ExpenseAdapter(Context context, OnItemsClick onItemsClick){
        this.context = context;
        this.onItemsClick = onItemsClick;
        expenseModelList = new ArrayList<>();

    }

    public void add(ExpenseModel expenseModel){
        expenseModelList.add(expenseModel);
        notifyDataSetChanged();
    }

    public void clear(){
        expenseModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ExpenseModel expenseModel = expenseModelList.get(position);
        holder.note.setText(expenseModel.getNote());
        holder.title.setText(expenseModel.getTitle());

        // Create a NumberFormat instance for Indian Rupees
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        numberFormat.setCurrency(Currency.getInstance("INR"));

        // Format the amount as Indian Rupees
        String rupees = numberFormat.format(expenseModel.getAmount());
        if (expenseModel.getType().equals("Expense")) {
            holder.amount.setTextColor(Color.RED);
        } else {
            holder.amount.setTextColor(Color.GREEN);
        }
        holder.amount.setText(rupees);

        holder.itemView.setOnClickListener(view -> onItemsClick.onClick(expenseModel));
        holder.date.setText(formatTime(expenseModel.getTime()));


    }

    public String formatTime(long time){
        //
        long timestampInMillis = time; // Replace with your timestamp in milliseconds

        long currentTimeInMillis = System.currentTimeMillis();
        long differenceInMillis = currentTimeInMillis - timestampInMillis;
        long hoursDifference = TimeUnit.MILLISECONDS.toHours(differenceInMillis);

        String formattedDateTime;

        if (hoursDifference < 24) {
            // Represent as time
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
            formattedDateTime = timeFormat.format(new Date(timestampInMillis));
        } else if (hoursDifference < 24*365){
            // Represent as date
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd MMM",Locale.getDefault());
            formattedDateTime = dateFormat.format(new Date(timestampInMillis));
        } else {
            // Represent as date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy",Locale.getDefault());
            formattedDateTime = dateFormat.format(new Date(timestampInMillis));
        }
        return formattedDateTime;
    }

    @Override
    public int getItemCount() {

        return expenseModelList.size();
    }

    public ExpenseModel getItem(int position) {
        return expenseModelList.get(position);
    }


    public void removeItem(int position) {
        expenseModelList.remove(position);
    }

    public void insertItem(ExpenseModel deletedExpenseModel, int position) {
        expenseModelList.add(position,deletedExpenseModel);
    }

    public List<ExpenseModel> getExpenseModelsList() {
        return expenseModelList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView note, title, category, amount, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.note_view);
            amount = itemView.findViewById(R.id.payment_amount);
            title = itemView.findViewById(R.id.payment_title);
            date = itemView.findViewById(R.id.date_view);
        }
    }

}












