package com.subodh.spendy;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.annotation.Nullable;

public class SmsService extends Service {


    SmsReceiver smsReceiver;
    Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.i(TAG, "Service is running ! ");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        smsReceiver = new SmsReceiver();
        Log.i(TAG, "Sms Receiver is called ! ");
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, filter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
        super.onDestroy();
    }
}



























