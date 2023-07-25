package com.subodh.spendy;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.subodh.spendy.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    private static final String NOTIFICAITON_CHANNEL_ID = "Normal";
    private static final int NOTIFICAITON_ID = 1001;

    private  static int login_signup = 0;

    ActivityLoginBinding binding;

    FirebaseAuth mAuth;

    ProgressBar progressBar;

    Intent intent_main;

    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        progressBar = binding.progressbar;

        intent_main = new Intent(Login.this, MainActivity.class);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification notification ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.icon_3)
                    .setContentTitle("New notification")
                    .setSubText("This is a sub text of notification")
                    .setChannelId(NOTIFICAITON_CHANNEL_ID)
                    .build();
            nm.createNotificationChannel(new NotificationChannel(NOTIFICAITON_CHANNEL_ID,"Login", NotificationManager.IMPORTANCE_HIGH));


        }else{
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.icon_3)
                    .setContentTitle("New notification")
                    .setSubText("This is a sub text of notification")
                    .build();
        }

        nm.notify(NOTIFICAITON_ID,notification);

        binding.newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(login_signup==0){
                    binding.loginTitle.setText("Register");
                    binding.loginBtn.setText("Register");
                    binding.newUser.setText("Already registerd Login here !");
                    login_signup = 1;
                }else{
                    binding.loginTitle.setText("Login");
                    binding.loginBtn.setText("Login");
                    binding.newUser.setText("New on Spendy Register here !");
                    login_signup = 0;
                }

            }
        });


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email, password;
                email = binding.enterEmail.getText().toString();
                password = binding.enterPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
//                    Toast.makeText(Login.this, "Enter email !", Toast.LENGTH_SHORT).show();
                    binding.enterEmail.setError("Required Field");

                } else if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(Login.this, "Enter password !", Toast.LENGTH_SHORT).show();
                    binding.enterPassword.setError("Required Field");
                }else {

                    if (login_signup == 1) {
                        register();
                    } else {
                        signIn();
                    }
                }

            }
        });

        binding.conitnue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("continue clickec !");
                signInAnanymously();
            }
        });


        binding.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            firestore = FirebaseFirestore.getInstance();
            copyDocuments();

            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in
            if (currentUser.isAnonymous()) {
                // User is logged in anonymously
                // Handle the case here (e.g., show anonymous user UI or specific actions)
//                Toast.makeText(this, "Logged in as a Guest !", Toast.LENGTH_SHORT).show();

            } else {
                // User is logged in with email or other authentication provider
                // Handle the case here (e.g., show regular user UI or specific actions)
                // You can also check the user's email if needed
                String email = currentUser.getEmail();

                if (email != null) {
//                    Toast.makeText(this, "You are Already Logged in", Toast.LENGTH_SHORT).show();
                    binding.newUser.setVisibility(View.GONE);
                    binding.enterEmail.setVisibility(View.GONE);
                    binding.enterPassword.setVisibility(View.GONE);
                    binding.loginTitle.setText("Already Logged in ");
                    binding.loginBtn.setText("Logout");
                    binding.copyBtn.setVisibility(View.VISIBLE);

                    binding.loginBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signOut();
                            signInAnanymously();
                        }
                    });
                    // User is logged in with email
                }
            }
        } else {
            // User is not logged in
            // Handle the case here (e.g., show login screen or redirect to login activity)
        }
    }

    public void signInAnanymously(){

            FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .addOnSuccessListener(authResult -> {
//                        Toast.makeText(this, "Login succeed !", Toast.LENGTH_SHORT).show();
                        System.out.println("Signed as Anonymous");
                        startActivity(intent_main);
                        finish();
                    })
                    .addOnFailureListener(e -> {
//                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Signed as Anonymous");
                    });


    }

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
//        Toast.makeText(this, "SignOut !", Toast.LENGTH_SHORT).show();
    }


    public  void signIn(){

        progressBar.setVisibility(View.VISIBLE);
        String email, password;
        email = binding.enterEmail.getText().toString();
        password = binding.enterPassword.getText().toString();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "SingIn succeed !");
//                                Toast.makeText(Login.this, "SingIn succeed !", Toast.LENGTH_SHORT).show();
                                binding.invalidError.setVisibility(View.GONE);

                                startActivity(intent_main);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                binding.invalidError.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);

                        }

                    });
    }

    public void register(){

        progressBar.setVisibility(View.VISIBLE);
        String email, password;
        email = binding.enterEmail.getText().toString();
        password = binding.enterPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
//            Toast.makeText(Login.this, "Enter email !", Toast.LENGTH_SHORT).show();
            binding.enterEmail.setError("Required Field");

        } else if (TextUtils.isEmpty(password)) {
//            Toast.makeText(Login.this, "Enter password !", Toast.LENGTH_SHORT).show();
            binding.enterPassword.setError("Required Field");
        }else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(Login.this, "Acount Created !", Toast.LENGTH_SHORT).show();
                                binding.invalidError.setVisibility(View.GONE);
                                login_signup = 0;
                                startActivity(intent_main);
                                finish();

                            } else {
//                                Toast.makeText(Login.this, "Login Failed !", Toast.LENGTH_SHORT).show();
                                binding.invalidError.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

    }


    public void copyDocuments() {
        final String sourceCollection = "RIPrhfph98WgnMHFldGCpLc2aUd2";
        final String destinationCollection = FirebaseAuth.getInstance().getUid();

        firestore.collection(sourceCollection).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Get the data from the source document
                                Object data = document.getData();

                                // Save the data to the destination collection
                                firestore.collection(destinationCollection)
                                        .document(document.getId())
                                        .set(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Document copied successfully
                                                    Log.d("Copy", "Document copied successfully");
                                                } else {
                                                    // Error copying document
                                                    Log.e("Copy", "Error copying document", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            // Error getting documents
                            Log.e("Copy", "Error getting documents", task.getException());
                        }
                    }
                });

    }
}