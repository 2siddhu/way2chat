package com.siddhartha.way2talk;

import com.siddhartha.way2talk.MainActivity;
import com.siddhartha.way2talk.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {
            
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                
                for (int i = 0; i < pdusObj.length; i++) {
                    
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                            Intent notificationIntent = new Intent(context, MainActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                     NotificationManager notificationManager = (NotificationManager) context
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                     Notification noti = new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setTicker("ticker")
                                        .setContentTitle("Way2Chat")
                                        .setContentText("senderNum: "+ senderNum + ", message: " + message)
                                        .setContentIntent(contentIntent)
                                        .setAutoCancel(true).build();
                    notificationManager.notify(0, noti);
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, 
                                 "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();
                    
                } 
              }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
            
        }
    }
}
