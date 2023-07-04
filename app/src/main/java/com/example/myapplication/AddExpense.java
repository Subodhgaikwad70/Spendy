package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.myapplication.databinding.ActivityAddExpenseBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

public class AddExpense extends AppCompatActivity  {


    private ExpenseModel expenseModel;
    ActivityAddExpenseBinding binding;
    String type;
    String category;
    String[] categories = new String[] {"Utilities", "Borrow", "Payment", "Food and Dinning","Travel","Shopping","Entertainment","Groceries","Miscellaneous"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        expenseModel = (ExpenseModel) getIntent().getSerializableExtra("model");


        if( expenseModel!=null ){
            binding.enterTitle.setText(expenseModel.getTitle());
            binding.enterAmount.setText(String.valueOf(expenseModel.getAmount()));
            binding.enterNote.setText(expenseModel.getNote());
            binding.filledExposedDropdown.setText(expenseModel.getCategory());
            if(expenseModel.getType().equals("Income")){
                binding.incomeRadio.setChecked(true);
            }else{
                binding.expenseRadio.setChecked(true);
            }

            binding.okButton.setText("update");
        }else{
            binding.expenseRadio.setChecked(true);
        }

        binding.incomeRadio.setOnClickListener(view -> type = "Income");
        binding.expenseRadio.setOnClickListener(view -> type = "Expense");


        Button cancel_btn = findViewById(R.id.cancel_button);
        Button ok_btn = findViewById(R.id.ok_button);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, categories);

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        editTextFilledExposedDropdown.setOnItemClickListener((parent, view, position, id) -> {
            category = (String) parent.getItemAtPosition(position);
            System.out.println("selected item is : "+category);
            Toast.makeText(this, "selected item is : "+category, Toast.LENGTH_SHORT).show();
        });

        cancel_btn.setOnClickListener(view -> {
            expenseModel = null;
            finish();
        });

        ok_btn.setOnClickListener(view -> {

            if(binding.enterTitle.getText().toString().trim().length() == 0){
                binding.enterTitle.setError("Required Field");

            }else if(binding.enterAmount.getText().toString().trim().length() == 0){
                binding.enterAmount.setError("Required Field");

            }else if(binding.filledExposedDropdown.getText().toString().trim().length() == 0){
                binding.filledExposedDropdown.setError("Required Field");
            }else{
                if( expenseModel == null ){
                    createExpense();
                }else{
                    updateExpense();
                }

                Intent expense_view_intent = new Intent(this, ExpenseView.class);
                expense_view_intent.putExtra("model",expenseModel);
                finish();
            }
        });

//        binding.enterAmount.addTextChangedListener(new TextWatcher() {
//            private DecimalFormat decimalFormat = new DecimalFormat("#,##,##,###");
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // This method is called before the text is changed
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // This method is called when the text is being changed
//                String newText = s.toString(); // Get the updated text
//                // Perform any required text manipulation or processing
////                String modifiedText = newText.toUpperCase(); // Example: convert text to uppercase
//
//                String modifiedText = decimalFormat.format(Integer.parseInt(newText));
//                binding.enterAmount.setText(modifiedText); // Update the text in the EditText
//                binding.enterAmount.setSelection(modifiedText.length()); // Set the cursor position to the end
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // This method is called after the text has been changed
//            }
//        });


    }

    @Override
    public void onBackPressed() {
        // Perform your desired functionality here
        expenseModel = null;
        super.onBackPressed();
    }

    private void createExpense() {

        String expenseId = UUID.randomUUID().toString();
        String title = binding.enterTitle.getText().toString();
        String amount = binding.enterAmount.getText().toString();
        String type ;
        String note = binding.enterNote.getText().toString();
//        category = null;
        boolean incomeChecked = binding.incomeRadio.isChecked();

        if(incomeChecked){
            type = "Income";
        }else {
            type = "Expense";
        }

        ExpenseModel expenseModel = new ExpenseModel(expenseId,title,Long.parseLong(amount),category,type,note,Calendar.getInstance().getTimeInMillis(),FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .document(expenseId)
                .set(expenseModel);
        finish();
    }

    private void updateExpense() {

        String title = binding.enterTitle.getText().toString();
        String amount = binding.enterAmount.getText().toString();
        String type ;
        String note = binding.enterNote.getText().toString();
//        category = null;
        boolean incomeChecked = binding.incomeRadio.isChecked();

        if(incomeChecked){
            type = "Income";
        }else{
            type = "Expense";
        }


        ExpenseModel model = new ExpenseModel(expenseModel.getExpenseId(),title,Long.parseLong(amount),category,type,note,expenseModel.getTime(),FirebaseAuth.getInstance().getUid());

        MainAcitvity mainAcitvity = new MainAcitvity();
        mainAcitvity.deleteExpense(expenseModel);


        FirebaseFirestore
                .getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .document(expenseModel.getExpenseId())
                .set(model);

//        mainAcitvity.getData();
        finish();
        expenseModel = null;
    }

}



















