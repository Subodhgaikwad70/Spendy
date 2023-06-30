package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class AddExpense extends AppCompatActivity {

    ActivityAddExpenseBinding binding;
    public String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.incomeRadio.setChecked(true);

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Income";
            }
        });

        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "Expense";
            }
        });



        // Below is custom code

        Button cancel_btn = findViewById(R.id.cancel_button);
        Button ok_btn = findViewById(R.id.ok_button);


        String[] type = new String[] {"Utilities", "Borrow", "Payment", "Party","Miscellaneous"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, type);

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);


        Intent intent_main =new Intent(this,MainAcitvity.class);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExpense();

                startActivity(intent_main);
            }


        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(intent_main);
            }
        });





    }

    private void createExpense() {

        String expenseId = UUID.randomUUID().toString();
        String title = binding.enterTitle.getText().toString();
        String amount = binding.enterAmount.getText().toString();
        String type ;
        String note = binding.enterNote.getText().toString();

        String category = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            category = findViewById(R.id.filled_exposed_dropdown).getAutofillValue().toString();
        }
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

        ExpenseModel expenseModel = new ExpenseModel(expenseId,title,Long.parseLong(amount),type,note,category,Calendar.getInstance().getTimeInMillis());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseId)
                .set(expenseModel);
        finish();
    }


}



















