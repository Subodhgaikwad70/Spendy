package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;

import java.util.ArrayList;

public class Transactions extends AppCompatActivity {


    ArrayList<ExpenseModel> expenses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //show the activity in full screen

        RecyclerView recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        transactions.add(new Transaction("Payment1",800,"note 1"));
//        transactions.add(new Transaction("Payment2",500,"note 2"));
//        transactions.add(new Transaction("Payment3",400,"note 3"));
//        transactions.add(new Transaction("Payment4",500,"note 4"));
//        transactions.add(new Transaction("Payment5",600,"note 5"));

        RecyclerTransactionAdapter adapter = new RecyclerTransactionAdapter(this,expenses);
        recyclerView.setAdapter(adapter);
    }
}

















