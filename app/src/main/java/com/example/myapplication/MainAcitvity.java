package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.myapplication.databinding.ActivityMainAcitvityBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainAcitvity extends AppCompatActivity implements OnItemsClick{
    ActivityMainAcitvityBinding binding;
    private ExpenseAdapter expenseAdapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseAdapter = new ExpenseAdapter(this,this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);


        FloatingActionButton circular_add_button = findViewById(R.id.circular_add_button);

        intent=new Intent(this, AddExpense.class);
        circular_add_button.setOnClickListener(view -> startActivity(intent));

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

    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }

}
















