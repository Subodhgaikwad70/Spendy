package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.databinding.ActivityProgressBinding;

public class Progress extends AppCompatActivity {

    ActivityProgressBinding binding;
    private int progress = 0;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Button buttonIncrement;
    private Button buttonDecrement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        progressBar = findViewById(R.id.progress_bar);
        textViewProgress = findViewById(R.id.text_view_progress);
        buttonIncrement = binding.buttonIncr;
        buttonDecrement = binding.buttonDecr;

        updateProgressBar();

        buttonIncrement.setOnClickListener(v -> {
            if (progress <= 90) {
                progress += 10;
                updateProgressBar();
            }
        });

        buttonDecrement.setOnClickListener(v -> {
            if (progress >= 10) {
                progress -= 10;
                updateProgressBar();
            }
        });
    }

    private void updateProgressBar() {
        progressBar.setProgress(progress);
        textViewProgress.setText(progress + "%");

    }
}