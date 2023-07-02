package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
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

    ArrayList<ExpenseModel> expenses = new ArrayList<>();
    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        expenseAdapter = new ExpenseAdapter(this,this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);
    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
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

}

















