package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import java.util.ArrayList;

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
}

















