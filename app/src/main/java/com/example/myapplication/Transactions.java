package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityAddExpenseBinding;
import com.example.myapplication.databinding.ActivityTransactionsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transactions extends AppCompatActivity implements OnItemsClick {

    Intent intent;

    private ExpenseAdapter expenseAdapter;

    private String filterby;

    public double expense = 0;
    public double income =0;
    ArrayList<String> categories = new ArrayList<>();
    RecyclerView recyclerView;
    ActivityTransactionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTransactionsBinding binding = ActivityTransactionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        expenseAdapter = new ExpenseAdapter(this,this);
        recyclerView = binding.recyclerView2;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        AddExpense addExpense = new AddExpense();
        String[] temp = addExpense.categories;


        categories.add("Filter");
        for (String s : temp){
            categories.add(s);
        }

        String[] all_view = new String[] {"All","Income","Expense"};

        Spinner filterView = binding.filterView;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, categories);
        filterView.setAdapter(adapter);

        Spinner allView = binding.allView;
        ArrayAdapter<String> all_view_adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, all_view);
        allView.setAdapter(all_view_adapter);

        allView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterby = (String) adapterView.getItemAtPosition(i);
                if (filterby.equals("All")){
                    getData();
                    recyclerView.setAdapter(expenseAdapter);
                }else {
                    getFilteredData("type",filterby);
                    recyclerView.setAdapter(expenseAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        filterView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filterby = (String) adapterView.getItemAtPosition(i);
                if (filterby.equals("Filter")){
                    getData();
                    recyclerView.setAdapter(expenseAdapter);
                }else {
                    getFilteredData("category",filterby);
                    recyclerView.setAdapter(expenseAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onClick(ExpenseModel expenseModel) {

        Intent intent_expense_view = new Intent(Transactions.this,ExpenseView.class);
        intent_expense_view.putExtra("model",expenseModel);
        startActivity(intent_expense_view);

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
                    .addOnFailureListener(e -> Toast.makeText(Transactions.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        FirebaseFirestore.getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseAdapter.clear();
                    List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot ds:dsList) {
                        ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                        expenseAdapter.add(expenseModel);
                    }
                });
    }

    public void getFilteredData(String filterType, String filterby) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .whereEqualTo(filterType, filterby)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseAdapter.clear();
                    List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();

                    // Sort the documents based on the "time" field in descending order
                    Collections.sort(dsList, (doc1, doc2) -> {
                        long time1 = doc1.getLong("time");
                        long time2 = doc2.getLong("time");
                        return Long.compare(time2, time1); // Descending order
                    });

                    for (DocumentSnapshot ds : dsList) {
                        ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);

                        if (expenseModel.getType().equals("Income")){
                            income+=expenseModel.getAmount();
                        }else{
                            expense+=expenseModel.getAmount();
                        }

                        expenseAdapter.add(expenseModel);
                    }
                    expenseAdapter.notifyDataSetChanged();
                    Toast.makeText(Transactions.this, "Success!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
//                                Toast.makeText(Transactions.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                    expenseAdapter.clear();
                    expenseAdapter.notifyDataSetChanged();
                    Toast.makeText(Transactions.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                });

    }

    public double getExpense() {
        return expense;
    }

    public double getIncome() {
        return income;
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
                    MainAcitvity mainAcitvity = new MainAcitvity();
                    mainAcitvity.deleteExpense(deletedExpenseModel);
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
                    });
                    snackbar.show();
                    break;
            }
        }
    };

}

















