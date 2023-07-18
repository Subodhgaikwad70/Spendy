package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityProgressBinding;

import java.io.Serializable;

public class Progress extends AppCompatActivity implements OnProgressClick{

    ActivityProgressBinding binding;
    private ProgressAdapter progressAdapter;

    RecyclerView progressRecycler;

    private int progress = 0;

    private ProgressBar progressBar;
    private TextView textViewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressAdapter = new ProgressAdapter(this,this);
        progressRecycler = binding.progressRecycler;
        progressRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        progressRecycler.setAdapter(progressAdapter);

        for(String category : MainAcitvity.categories.keySet()){
            int value = MainAcitvity.categories.get(category);
            createProgress(category,value);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void createProgress(String category,int progress) {
//        category = "Utilities";
//        progress = 75;

        ProgressModel progressModel = new ProgressModel(category, progress);

        progressAdapter.add(progressModel);

    }


    @Override
    public void onClick(ProgressModel progressModel) {
        Intent transaction_intent = new Intent(Progress.this,Transactions.class);
        transaction_intent.putExtra("category", progressModel.getName());
        startActivity(transaction_intent);
    }


}















