package com.subodh.spendy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.subodh.spendy.databinding.ActivityExpenseViewBinding;

public class ExpenseView extends AppCompatActivity {
    ActivityExpenseViewBinding binding;
    private ExpenseModel expenseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseModel = (ExpenseModel) getIntent().getSerializableExtra("model");

        binding.titleView.setText(expenseModel.getTitle());
        binding.amountView.setText("Amount    :  "+String.valueOf(expenseModel.getAmount()));
        binding.categoryView.setText("Category  :  "+expenseModel.getCategory());
        binding.typeView.setText("Type         :  "+expenseModel.getType());
        binding.noteView.setText("Note         :  "+expenseModel.getNote());


        // Create an Intent to start the activity
        Intent intent = new Intent(this, AddExpense.class);

        // Start the activity with the launcher

        binding.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent.putExtra("model",expenseModel);
                startActivity(intent);
                finish();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

















