package com.subodh.spendy;

import static com.subodh.spendy.MainActivity.categories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.subodh.spendy.databinding.ActivityProgressBinding;

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
        progressRecycler.setLayoutManager(new LinearLayoutManager(this));
//        progressRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        progressRecycler.setAdapter(progressAdapter);

        for(String category : categories.keySet()){
            double value = categories.get(category);
            if (value > 0.0){
                createProgress(category,value);
            }
        }

//        Toast.makeText(this, "Categories : "+categories, Toast.LENGTH_SHORT).show();
        System.out.println("Categories : "+categories);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void createProgress(String category,double progress) {

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















