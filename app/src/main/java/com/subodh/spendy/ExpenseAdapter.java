package com.subodh.spendy;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    ProgressBar progressBar;
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

//        holder.category_icon.setImageResource(setIcon(expenseModel.getCategory()));

//        Glide.with(holder.itemView)
//                .load(R.drawable.icon_3)
//                .circleCrop()
//                .into(holder.category_icon);

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



    // Viewholder model
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView note, title, category, amount, date;
        private ImageView category_icon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            category_icon = itemView.findViewById(R.id.category_image);
            progressBar = itemView.findViewById(R.id.progress_bar0);
            note = itemView.findViewById(R.id.note_view);
            amount = itemView.findViewById(R.id.payment_amount);
            title = itemView.findViewById(R.id.payment_title);
            date = itemView.findViewById(R.id.date_view);
        }
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


    private int setIcon(String category){
        if (category.equals("Utilities"))
            return R.drawable.utilities01_24;
        else if (category.equals("Borrow"))
            return R.drawable.borrows_24;
        else if (category.equals("Payment"))
            return R.drawable.payment_24;
        else if (category.equals("Food and Dinning"))
            return R.drawable.food_and_dining01_24;
        else if (category.equals("Travel"))
            return R.drawable.travel_24;
        else if (category.equals("Shopping"))
            return R.drawable.shopping_24;
        else if (category.equals("Entertainment"))
            return R.drawable.entertainment_24;
        else if (category.equals("Groceries"))
            return R.drawable.groceries_24;
        else if (category.equals("Miscellaneous"))
            return R.drawable.miscellaneous_24;
        else
            return R.drawable.icon_3;
    }


}












