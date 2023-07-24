import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsService extends Service {

    private BroadcastReceiver smsReceiver;
    private static final int NOTIFICATION_ID = 123; // Unique ID for the notification
    private static final String CHANNEL_ID = "sms_service_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        registerSmsReceiver();

        // Start the service as a foreground service (for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            Notification notification = createNotification();
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // Service will be restarted if it gets killed by the system
    }

    private void registerSmsReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
                    SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                    for (SmsMessage smsMessage : smsMessages) {
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        // Add the new SMS message to your local list or database
                        // Update the UI to show the new message as unread
                    }
                }
            }
        };

        // Register the BroadcastReceiver to listen for SMS_RECEIVED_ACTION
        IntentFilter intentFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // For Android 8.0 and above, create a notification channel and notification
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SMS Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("SMS Service is running")
                .setContentText("Listening for new SMS messages")
                .setSmallIcon(R.drawable.ic_notification);

        return builder.build();
    }
}
