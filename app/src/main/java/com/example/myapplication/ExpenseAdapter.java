package com.example.myapplication;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

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
        holder.amount.setText(rupees);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemsClick.onClick(expenseModel);
            }
        });

    }

    @Override
    public int getItemCount() {

        return expenseModelList.size();
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












