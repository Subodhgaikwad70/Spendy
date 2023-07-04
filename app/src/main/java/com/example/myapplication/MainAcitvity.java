package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.databinding.ActivityMainAcitvityBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MainAcitvity extends AppCompatActivity implements OnItemsClick{
    ActivityMainAcitvityBinding binding;
    private ExpenseAdapter expenseAdapter;
    Intent intent;
    private GestureDetector gestureDetector;
    private long income = 0, expense=0;

    RecyclerView recyclerView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView imageView = binding.avatarView;
        Glide.with(this)
                .load(R.drawable.icon_3)
                .circleCrop()
                .into(imageView);


        expenseAdapter = new ExpenseAdapter(this,this);
        recyclerView = binding.recyclerView1;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);


        FloatingActionButton circular_add_button = findViewById(R.id.circular_add_button);

        intent=new Intent(this, AddExpense.class);

        circular_add_button.setOnClickListener(view -> {
            ExpenseModel expenseModel=null;
            intent.putExtra("model",expenseModel);
            startActivity(intent);

        });

        // ...
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Calculate the swipe direction based on the Y coordinates of the MotionEvent objects
                float deltaY = e2.getY() - e1.getY();
                if (deltaY < 0) {
                    // Swiped from bottom to top (swipe up)
                    // Start the new activity here
                    Bundle b = ActivityOptions.makeSceneTransitionAnimation(MainAcitvity.this).toBundle();
                    startActivity(new Intent(MainAcitvity.this, Transactions.class),b);
                    return true;
                }
                return false;
            }
        });

        binding.headerConstraint.setOnTouchListener((v, event) -> {
            // Pass the touch event to the GestureDetector
            gestureDetector.onTouchEvent(event);
            return true;
        });


        binding.editIcon.setOnClickListener(view -> {
            intent = new Intent(MainAcitvity.this, Progress.class);
            startActivity(intent);
        });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(authResult -> {

                    })
                    .addOnFailureListener(e -> Toast.makeText(MainAcitvity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income =0;
        expense = 0;
        getData();
    }

    public void getData() {
        income =0;
        expense = 0;
        FirebaseFirestore.getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseAdapter.clear();
                    List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();

                    // Add the sorted expenseModels to the adapter
                    for (DocumentSnapshot ds:dsList) {
                        ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);

                        if (expenseModel.getType().equals("Income")){
                            income+=expenseModel.getAmount();
                        }else{
                            expense+=expenseModel.getAmount();
                        }
                        long total = income-expense;

                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                        numberFormat.setCurrency(Currency.getInstance("INR"));

                        // Format the amount as Indian Rupees
                        String rupees = numberFormat.format(total);
                        binding.totalSpend.setText(""+rupees);

                        if (expense > income) {
                            binding.totalSpend.setTextColor(Color.RED);
                        } else {
                            binding.totalSpend.setTextColor(Color.GREEN);
                        }
                        expenseAdapter.add(expenseModel);
                    }
                    setUpGraph();
                });
    }

    private void setUpGraph(){
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (income!=0){
            pieEntryList.add(new PieEntry(income,"Income"));
            colorsList.add(ContextCompat.getColor(this, R.color.green_pie));
        }
        if (expense!=0){
            pieEntryList.add(new PieEntry(expense,"Expense"));
            colorsList.add(ContextCompat.getColor(this, R.color.red_pie));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList,String.valueOf(income-expense));
        pieDataSet.setColors(colorsList);
        PieData pieData = new PieData(pieDataSet);
        binding.piechart.setData(pieData);
        binding.piechart.invalidate();


    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        Intent expense_view_intent = new Intent(MainAcitvity.this,ExpenseView.class);
        expense_view_intent.putExtra("model",expenseModel);
        startActivity(expense_view_intent);
    }


    ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // Not used in this case, return false
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();


            switch (direction){
                case ItemTouchHelper.LEFT:
                    break;

                case ItemTouchHelper.RIGHT:
                    ExpenseModel deletedExpenseModel;
                    deletedExpenseModel = expenseAdapter.getItem(position);
//                    Toast.makeText(Transactions.this, "ExpenseId : "+deletedExpenseModel.getExpenseId(), Toast.LENGTH_SHORT).show();
                    expenseAdapter.removeItem(position);
                    deleteExpense(deletedExpenseModel);
                    changeTotal(deletedExpenseModel);
                    expenseAdapter.notifyItemRemoved(position);

                    // Show the Snackbar with an undo action
                    Snackbar snackbar = Snackbar.make(recyclerView, ""+deletedExpenseModel.getTitle()+" is deleted !", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", v -> {
                        // Handle the undo action here

                        // For example, to restore the deleted item:
                        expenseAdapter.insertItem(deletedExpenseModel,position);
                        expenseAdapter.notifyItemInserted(position);
                        FirebaseFirestore
                                .getInstance()
                                .collection(FirebaseAuth.getInstance().getUid())
                                .document(deletedExpenseModel.getExpenseId())
                                .set(deletedExpenseModel);
                        changeTotal(deletedExpenseModel);
                        getData();
                    });
                    snackbar.setDuration(3000);
                    snackbar.show();
                    break;
            }
        }
    };



    public void deleteExpense(ExpenseModel expenseModel){
        FirebaseFirestore
                .getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .document(expenseModel.getExpenseId())
                .delete();

//        getData();
//        TextView totalspendview = findViewById(R.id.totalSpend);
//        String totalspend = totalspendview.getText().toString();
//        totalspend = totalspend.replace("₹", "").replace(",", "");
//        double updatedspend=0;
//        if(expenseModel.getType().equals("Income")){
//            updatedspend = Double.parseDouble(totalspend) - expenseModel.getAmount();
//        }else{
//            updatedspend = Double.parseDouble(totalspend) + expenseModel.getAmount();
//        }
//
//        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
//        numberFormat.setCurrency(Currency.getInstance("INR"));
//        String rupees = numberFormat.format(updatedspend);
//        binding.totalSpend.setText(rupees);
    }
    void changeTotal(ExpenseModel expenseModel){

        TextView totalspendview = findViewById(R.id.totalSpend);
        String totalspend = totalspendview.getText().toString();
        totalspend = totalspend.replace("₹", "").replace(",", "");
        double updatedspend=0;
        if(expenseModel.getType().equals("Income")){
            updatedspend = Double.parseDouble(totalspend) - expenseModel.getAmount();
        }else{
            updatedspend = Double.parseDouble(totalspend) + expenseModel.getAmount();
        }

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        numberFormat.setCurrency(Currency.getInstance("INR"));
        String rupees = numberFormat.format(updatedspend);
        binding.totalSpend.setText(rupees);

    }

}
















