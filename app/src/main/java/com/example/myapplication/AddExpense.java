package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityAddExpenseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class AddExpense extends AppCompatActivity  {

//    implements AdapterView.OnItemSelectedListener

    ActivityAddExpenseBinding binding;
    String type;
    String category;
    String[] categories = new String[] {"Utilities", "Borrow", "Payment", "Party","Miscellaneous"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.incomeRadio.setChecked(true);

        binding.incomeRadio.setOnClickListener(view -> type = "Income");

        binding.expenseRadio.setOnClickListener(view -> type = "Expense");



        // Below is custom code

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


        Intent intent_main =new Intent(this,MainAcitvity.class);

        cancel_btn.setOnClickListener(view -> startActivity(intent_main));

        ok_btn.setOnClickListener(view -> {
            createExpense();
            startActivity(intent_main);
        });





    }

//
//    @Override
//    public void onItemSelected(AdapterView<?> adapter, View view, int i, long l) {
//        category = categories[i];
//        Toast.makeText(this, ""+categories[i], Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }

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
        }else{
            type = "Expense";
        }

        if(amount.trim().length()==0){
            binding.enterAmount.setError("Required Field");
            return;
        }

        ExpenseModel expenseModel = new ExpenseModel(expenseId,title,Long.parseLong(amount),category,type,note,Calendar.getInstance().getTimeInMillis(),FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(expenseModel);
        finish();
    }


}



















