package com.subodh.spendy;


import static android.content.Context.NOTIFICATION_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private String TAG = "Spendy";
    private Context context;
    private String title;
    private String amountString;
    private String type;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context.getApplicationContext();

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            Log.i(TAG, "Broadcast Reciever Started !");

            for (SmsMessage smsMessage : smsMessages){
                String sender = smsMessage.getDisplayOriginatingAddress();
                String message = smsMessage.getMessageBody();

                Log.d(TAG, "onReceive: "+sender+"\n"+message);

//                HashMap<String,String> msg = new HashMap<>();
//
//                FirebaseFirestore
//                        .getInstance()
//                        .collection("SMS Received "+ FirebaseAuth.getInstance().getUid())
//                        .document(String.valueOf(Calendar.getInstance().getTimeInMillis()))
//                        .set(msg);
                parseSmsMessage(message);
                
            }



        }
        else {
            
        }
    }


    private void parseSmsMessage(String smsMessage) {
        // Pattern amountPattern = Pattern.compile("(Rs|₹)\\s?([^.\\s]?+(?:\\.\\d+)?)");
        Pattern amountPattern = Pattern.compile("(Rs|₹)\\s?\\.?(\\d+(?:\\,\\d+)?(?:\\.\\d{1,2})?)"); // Matches amounts with Rs/₹ symbol
        Pattern creditPattern = Pattern.compile("(credited|received|deposit|added)"); // Matches various credit keywords
        Pattern debitPattern = Pattern.compile("(sent|debited|withdrawn|deducted)"); // Matches various debit keywords

        Matcher amountMatcher = amountPattern.matcher(smsMessage);
        Matcher creditMatcher = creditPattern.matcher(smsMessage);
        Matcher debitMatcher = debitPattern.matcher(smsMessage);

        if (amountMatcher.find()) {
            String rupeeSymbol = amountMatcher.group(1);
            amountString = amountMatcher.group(2).replace(",", ""); // Remove commas from the amount string
            // String amountString = amountMatcher.group(2);

            System.out.println("\n\n"+amountString);

            double amount = Double.parseDouble(amountString);
//            System.out.println("Amount (in Rupees): " + amount);
            System.out.println(smsMessage);

            type = "Expense";
            title = "Debited";

            if (creditMatcher.find()) {
                title = "Credited";
                type="Income";
                System.out.println("Transaction Type: Credited");
            } else if (debitMatcher.find()) {
                System.out.println("Transaction Type: Debited");
            } else {
                System.out.println("Transaction Type: Unknown");
            }

//            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
//            numberFormat.setCurrency(Currency.getInstance("INR"));
//            String amount_in_rupees = numberFormat.format(amountString);


            String expenseId = UUID.randomUUID().toString();


            ExpenseModel addExpenseModel = new ExpenseModel(expenseId,title,Long.parseLong(amountString),null,type,title, Calendar.getInstance().getTimeInMillis(),FirebaseAuth.getInstance().getUid());
            MainActivity.expenseAdapter01.add(addExpenseModel);


            try {
                sendNotification(title,amountString);
            }catch (Exception e){
                Log.d(TAG, "Broadcast Notifiation Exception: "+e);
            }

//            MainActivity mainActivity = new MainActivity();
//            mainActivity.sendNotification(amountString,title);
//            pushNotification(type,amountString);
//            return newExpense;

        } else {
            System.out.println("\n\nAmount not found in the SMS message.");
            System.out.println(smsMessage);
//            return null;
        }

    }





    // Method to create a notification channel (required for Android 8.0 or higher)
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String title,String content){
        // Create a notification channel for devices running Android 8.0 (API level 26) or higher
            createNotificationChannel(context);

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(title);
            arrayList.add(amountString);
            arrayList.add(type);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putStringArrayListExtra("model_data",arrayList);
//        intent.putExtra("title",title);
//        intent.putExtra("amount",amountString);
//        intent.putExtra("type",type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        // Create a notification using NotificationCompat.Builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setSmallIcon(R.drawable.icon_3)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // Show the notification using NotificationManager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
    }



