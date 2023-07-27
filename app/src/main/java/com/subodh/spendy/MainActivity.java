package com.subodh.spendy;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.subodh.spendy.databinding.ActivityMainActivityBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.subodh.spendy.databinding.ActivityMainActivityBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnItemsClick{

    public static final String app = "Spendy";

    private long total;

    static List<DocumentSnapshot> dsList = new ArrayList<>();
    static HashMap<String, Double> categories = new HashMap<>();

    RecyclerView recyclerView,recyclerView01;

    ActivityMainActivityBinding binding;

    private ExpenseAdapter expenseAdapter;
    public static ExpenseAdapter expenseAdapter01;

    Intent intent;
    private GestureDetector gestureDetector;
    public static long income, expense;
    private float startY;
    public final static String NOTIFICATION_CHANNEL_ID = "Main";
    public final static int NOTIFICATION_ID = 100;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ImageView imageView = binding.avatarView;
        Glide.with(this)
                .load(R.drawable.icon_3)
                .circleCrop()
                .into(imageView);

        binding.avatarView.setOnClickListener(view -> {
            Intent login_intent = new Intent(MainActivity.this,Login.class);
            startActivity(login_intent);
        });


        expenseAdapter = new ExpenseAdapter(this,this);
        recyclerView = binding.recyclerView1;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expenseAdapter);


        expenseAdapter01 = new ExpenseAdapter(this,this);
        recyclerView01 = binding.newExpense;
        recyclerView01.setLayoutManager(new LinearLayoutManager(this));
        recyclerView01.setAdapter(expenseAdapter01);



        Intent add_exp_intent = new Intent(this, AddExpense.class);

        binding.circularAddButton.setOnClickListener(view -> {
            ExpenseModel expenseModel = null;
            add_exp_intent.putExtra("model", expenseModel);
            startActivity(add_exp_intent);

        });

        // ...
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                int SWIPE_THRESHOLD = 100;
                int SWIPE_VELOCITY_THRESHOLD = 100;

                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
//                            onSwipeRight();
                        } else {
//                            onSwipeLeft();
                            Bundle b = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                            startActivity(new Intent(MainActivity.this, Progress.class), b);
                        }
//                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
//                        onSwipeBottom();
                    } else {
//                        onSwipeTop();
                        Bundle b = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                        startActivity(new Intent(MainActivity.this, Transactions.class), b);
//                    result = true;

                    }
                }

                return false;
            }
        });

        binding.getRoot().setOnTouchListener((v, event) -> {
            // Pass the touch event to the GestureDetector
            gestureDetector.onTouchEvent(event);
            return true;
        });

        binding.editIcon.setOnClickListener(view -> {
            Intent progress_intent = new Intent(MainActivity.this, Progress.class);
            startActivity(progress_intent);
        });



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

//        ItemTouchHelper itemTouchHelper01 = new ItemTouchHelper(swipeToDeleteCallback);
//        itemTouchHelper01.attachToRecyclerView(recyclerView01);

        // Start the service
        Intent serviceIntent = new Intent(this, SmsService.class);
        startService(serviceIntent);

//        getBroadcastIntent();

        FirebaseApp.initializeApp(this); // Initialize FirebaseApp


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null) {
            String value1 = intent.getStringExtra("key1");
            int value2 = intent.getIntExtra("key2", 0);
            // ...

            // Update the values in your activity
            // For example, update TextViews or other UI elements

            expenseAdapter01.notifyDataSetChanged();
            Log.i(TAG, "handleNotificationIntent: "+intent.getStringExtra("title")+intent.getStringExtra("content"));
            // ...
        }
    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseApp.initializeApp(this); // Initialize FirebaseApp
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent intent_login = new Intent(MainActivity.this, Login.class);
            startActivity(intent_login);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income =0;
        expense = 0;
        getData();
    }

    public void getData() {
        income =0;
        expense = 0;

        FirebaseFirestore.getInstance()
                .collection(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseAdapter.clear();
                    categories.clear();
                    dsList = queryDocumentSnapshots.getDocuments();

                    // Add the sorted expenseModels to the adapter
                    for (DocumentSnapshot ds:dsList) {
                        ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);


                        String category = expenseModel.getCategory();


                        if (expenseModel.getType().equals("Income")){
                            income+=expenseModel.getAmount();
                        }else{
                            expense+=expenseModel.getAmount();

                            if (categories.containsKey(category)) {
                                double noOfCat = categories.get(category);
                                noOfCat = noOfCat + expenseModel.getAmount();
                                categories.put(category, noOfCat);
                            } else {
                                categories.put(category, 0.0);
                            }

                        }
                        total = income-expense;

                        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                        numberFormat.setCurrency(Currency.getInstance("INR"));

                        // Format the amount as Indian Rupees
                        String rupees = numberFormat.format(total);
                        binding.totalSpend.setText(""+rupees);

                        if (expense > income) {
                            binding.totalSpend.setTextColor(Color.RED);
                        } else {
                            binding.totalSpend.setTextColor(Color.GREEN);
                        }
                        expenseAdapter.add(expenseModel);
                    }

                    if (expenseAdapter.getItemCount() == 0) {
                        binding.emptyRecycler.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        binding.emptyRecycler.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    setUpGraph();
                    binding.progressBar0.setVisibility(View.GONE);
                });
    }

    private void setUpGraph(){
        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();
        if (income!=0){
            pieEntryList.add(new PieEntry(income,"Income"));
            colorsList.add(ContextCompat.getColor(this, R.color.green_pie));
        }
        if (expense!=0){
            pieEntryList.add(new PieEntry(expense,"Expense"));
            colorsList.add(ContextCompat.getColor(this, R.color.red_pie));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntryList,String.valueOf(income-expense));
        pieDataSet.setColors(colorsList);
        PieData pieData = new PieData(pieDataSet);
        binding.piechart.setData(pieData);
        binding.piechart.invalidate();

        if (income==0 && expense ==0) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.piechart.setVisibility(View.GONE);
        } else {
            binding.emptyView.setVisibility(View.GONE);
            binding.piechart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        Intent expense_view_intent = new Intent(MainActivity.this,ExpenseView.class);
        expense_view_intent.putExtra("model",expenseModel);
        startActivity(expense_view_intent);
    }


    ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // Not used in this case, return false
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();


            switch (direction){
                case ItemTouchHelper.LEFT:
                    break;

                case ItemTouchHelper.RIGHT:
                    ExpenseModel deletedExpenseModel;
                    deletedExpenseModel = expenseAdapter.getItem(position);
//                    Toast.makeText(Transactions.this, "ExpenseId : "+deletedExpenseModel.getExpenseId(), Toast.LENGTH_SHORT).show();
                    expenseAdapter.removeItem(position);
                    deleteExpense(deletedExpenseModel);
                    changeTotal(deletedExpenseModel);
                    expenseAdapter.notifyItemRemoved(position);

                    // Show the Snackbar with an undo action
                    Snackbar snackbar = Snackbar.make(recyclerView, ""+deletedExpenseModel.getTitle()+" is deleted !", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Undo", v -> {

                        expenseAdapter.insertItem(deletedExpenseModel,position);
                        expenseAdapter.notifyItemInserted(position);
                        FirebaseFirestore
                                .getInstance()
                                .collection(FirebaseAuth.getInstance().getUid())
                                .document(deletedExpenseModel.getExpenseId())
                                .set(deletedExpenseModel);
                        changeTotal(deletedExpenseModel);
                        getData();
                    });

                    snackbar.setDuration(3000);
                    snackbar.show();
                    break;
            }
        }
    };



    public void deleteExpense(ExpenseModel expenseModel){
        FirebaseFirestore
                .getInstance()
                .collection(FirebaseAuth.getInstance().getUid())
                .document(expenseModel.getExpenseId())
                .delete();

    }

    void changeTotal(ExpenseModel expenseModel){

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        numberFormat.setCurrency(Currency.getInstance("INR"));


        if(expenseModel.getType().equals("Income")){
            total = total - expenseModel.getAmount();
        }else{
            total = total + expenseModel.getAmount();
        }

        String rupees = numberFormat.format(total);
        binding.totalSpend.setText(""+rupees);

        if (expense > income) {
            binding.totalSpend.setTextColor(Color.RED);
        } else {
            binding.totalSpend.setTextColor(Color.GREEN);
        }
        expenseAdapter.add(expenseModel);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = e.getY();
                if (currentY < startY) {
                    return false; // Disable scroll when swiping up
                }
                break;
        }
        return super.onTouchEvent(e);
    }

//    public void sendNotification(String title,String content){
//
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            notification = new Notification.Builder(this)
//                    .setSmallIcon(R.drawable.icon_3)
//                    .setContentTitle(title)
//                    .setSubText(content)
//                    .setAutoCancel(true)
//                    .setChannelId(NOTIFICATION_CHANNEL_ID)
//                    .build();
//            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID,"newExpense", NotificationManager.IMPORTANCE_DEFAULT));
//        }
//
//        nm.notify(NOTIFICATION_ID,notification);
//    }

//    private void getBroadcastIntent(){
//
//        Intent intent1 = new Intent(this, SmsReceiver.class);
////        intent1.putExtra("Hii","hello");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
//        String content = intent1.getStringExtra("amount");
//        String title = intent1.getStringExtra("title");
//
//
//        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            notification = new Notification.Builder(this)
//                    .setSmallIcon(R.drawable.icon_3)
//                    .setContentTitle(title)
//                    .setSubText(content)
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true)
//                    .setChannelId(MainActivity.NOTIFICATION_CHANNEL_ID)
//                    .build();
//            nm.createNotificationChannel(new NotificationChannel(MainActivity.NOTIFICATION_CHANNEL_ID,"newExpense", NotificationManager.IMPORTANCE_DEFAULT));
//        }
//
//        nm.notify(MainActivity.NOTIFICATION_ID,notification);
//    }


}
















