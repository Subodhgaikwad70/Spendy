package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainAcitvityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainAcitvity extends AppCompatActivity {
    ActivityMainAcitvityBinding binding;
ArrayList<ExpenseModel> expenses = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainAcitvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        RecyclerView recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        transactions.add(new Transaction("Payment1",800,"note 1"));
//        transactions.add(new Transaction("Payment2",500,"note 2"));
//        transactions.add(new Transaction("Payment3",400,"note 3"));
//        transactions.add(new Transaction("Payment4",500,"note 4"));
//        transactions.add(new Transaction("Payment5",600,"note 5"));

        RecyclerTransactionAdapter adapter = new RecyclerTransactionAdapter(this,expenses);
        recyclerView.setAdapter(adapter);



        FloatingActionButton circular_add_button = findViewById(R.id.circular_add_button);

        Intent intent=new Intent(this, AddExpense.class);
        circular_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainAcitvity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
















