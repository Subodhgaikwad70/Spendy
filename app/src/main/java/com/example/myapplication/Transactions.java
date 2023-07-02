package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transactions extends AppCompatActivity implements OnItemsClick {

    Intent intent;

    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        expenseAdapter = new ExpenseAdapter(this,this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent = new Intent(Transactions.this,AddExpense.class);
        intent.putExtra("model",expenseModel);
        startActivity(intent);
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
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseAdapter.clear();
                    List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                    List<ExpenseModel> expenseModels = new ArrayList<>();

                    for (DocumentSnapshot ds : dsList) {
                        ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                        expenseModels.add(expenseModel);
                    }

                    // Sort the expenseModels list based on the timestamp
                    Collections.sort(expenseModels, (expense1, expense2) -> {
                        long time1 = expense1.getTime();
                        long time2 = expense2.getTime();
                        return Long.compare(time2, time1); // Sorting in descending order
                    });

                    // Add the sorted expenseModels to the adapter
                    for (ExpenseModel expenseModel : expenseModels) {
                        expenseAdapter.add(expenseModel);
                    }
                });
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
                    System.out.println(deletedExpenseModel.getExpenseId());
                    Toast.makeText(Transactions.this, "ExpenseId : "+deletedExpenseModel.getExpenseId(), Toast.LENGTH_SHORT).show();
                    deleteExpense(deletedExpenseModel);
                    expenseAdapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    private void deleteExpense(ExpenseModel expenseModel){
        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseModel.getExpenseId())
                .delete();
        finish();
    }


}

















