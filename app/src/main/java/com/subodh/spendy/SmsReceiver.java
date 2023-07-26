package com.subodh.spendy;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private String TAG = "Spendy";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

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
            String amountString = amountMatcher.group(2).replace(",", ""); // Remove commas from the amount string
            // String amountString = amountMatcher.group(2);

            System.out.println("\n\n"+amountString);

            double amount = Double.parseDouble(amountString);
//            System.out.println("Amount (in Rupees): " + amount);
            System.out.println(smsMessage);

            String type = "Expense",title = "Debited";

            if (creditMatcher.find()) {
                title = "Credited";
                type="Income";
                System.out.println("Transaction Type: Credited");
            } else if (debitMatcher.find()) {
                System.out.println("Transaction Type: Debited");
            } else {
                System.out.println("Transaction Type: Unknown");
            }


            String expenseId = UUID.randomUUID().toString();
            ExpenseModel addExpenseModel = new ExpenseModel(expenseId,title,Long.parseLong(amountString),null,type,null, Calendar.getInstance().getTimeInMillis(),null);
            MainActivity.expenseAdapter01.add(addExpenseModel);

//            pushNotification(type,amountString);
//            return newExpense;

        } else {
            System.out.println("\n\nAmount not found in the SMS message.");
            System.out.println(smsMessage);
//            return null;
        }

    }

}
