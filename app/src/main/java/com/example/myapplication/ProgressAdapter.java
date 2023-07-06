package com.example.myapplication;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;



public class ProgressAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {

    private Context context;
    private OnItemsClick onItemsClick;
    private List<ExpenseModel> progressModelList;

    public ProgressAdapter(Context context, OnItemsClick onItemsClick, List<ExpenseModel> progressModelList) {
        this.context = context;
        this.onItemsClick = onItemsClick;
        this.progressModelList = progressModelList;
    }

    public void add(ExpenseModel expenseModel){
        progressModelList.add(expenseModel);
        notifyDataSetChanged();
    }

    public void clear(){
        progressModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}