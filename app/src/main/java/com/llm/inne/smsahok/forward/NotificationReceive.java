package com.llm.inne.smsahok.forward;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.llm.inne.smsahok.SessionManager;
import com.llm.inne.smsahok.offline.DBController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by inne on 29/01/16.
 */
public class NotificationReceive  extends BroadcastReceiver{
    SessionManager user;
    public static final String ACTION = "inne.llm.RESEND";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION)){
            int notificationId = intent.getIntExtra("notificationId", 0);
            Log.i("Notification ID", String.valueOf(notificationId));
            user=new SessionManager(context);
            HashMap<String, String> users = user.getUserDetails();
            Bundle bundle = intent.getExtras();
            if(bundle!=null){
                String message = (String) bundle.get("message");
                String number = (String) bundle.get("contactNo");
                Log.i("Notif",message+" , "+ number);
                String JSON = "{\"message\":\"" + message + "\", \"contactNo\":" + "\"" + number + "\",\"phoneNumber\":\"" + users.get(SessionManager.KEY_NUMBER_FORMAT1) + "\"}";

                // Do what you want were.
                context.startService(new Intent(context, SMSForwardService.class).putExtra("JSON", JSON));

                // if you want cancel notification
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notificationId);
            }
        }


    }

}
