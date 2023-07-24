package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.MyViewHolder> {

    private Context context;
    private OnProgressClick onProgressClick;
    private List<ProgressModel> progressModelList;

    public ProgressAdapter(Context context,OnProgressClick onProgressClick) {
        this.context = context;
        this.onProgressClick = onProgressClick;
        progressModelList = new ArrayList<>();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_card_rectangular,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressAdapter.MyViewHolder holder, int position) {
        double total_expense = MainAcitvity.expense;

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
//        numberFormat.setCurrency(Currency.getInstance("INR"));

        ProgressModel progressModel = progressModelList.get(position);
        holder.name.setText(progressModel.getName());
        holder.value.setText((Math.ceil((progressModel.getProgress())*100/total_expense))+"%");
        holder.progressBar.setProgress((int) Math.ceil((progressModel.getProgress())*100/total_expense));
        holder.amount.setText(numberFormat.format((int) progressModel.getProgress())+"/"+numberFormat.format(((int) total_expense)));
        holder.itemView.setOnClickListener(view -> onProgressClick.onClick(progressModel));

    }

    public void add(ProgressModel progressModel){
        progressModelList.add(progressModel);
        notifyDataSetChanged();
    }

    public void clear(){
        progressModelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return progressModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name,value,amount;
        private ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.progress_name);
            value = itemView.findViewById(R.id.progress_value);
            amount = itemView.findViewById(R.id.amount);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }








}
