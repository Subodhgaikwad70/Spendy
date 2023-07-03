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
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityAddExpenseBinding;
import com.example.myapplication.databinding.ActivityTransactionsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transactions extends AppCompatActivity implements OnItemsClick {

    Intent intent;

    private ExpenseAdapter expenseAdapter;
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

















