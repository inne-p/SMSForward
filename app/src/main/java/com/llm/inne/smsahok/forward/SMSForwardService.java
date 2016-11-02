package com.llm.inne.smsahok.forward;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.llm.inne.smsahok.R;
import com.llm.inne.smsahok.SessionManager;
import com.llm.inne.smsahok.offline.DBController;
import com.llm.inne.smsahok.offline.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by inne on 13/01/16.
 */
public class SMSForwardService extends Service {
    String message="", number ="",phoneNumber="";
    BroadcastReceiver smsSentReceiver;
    JSONArray jsonData;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        String JSONextra =bundle.getString("JSON");
        try {

            JSONObject jsonObj = new JSONObject(JSONextra);
            //JSONObject data = jsonObj.getJSONObject("data");

            //jsonObj.getJSONObject("data");
            Log.i("JSON Pesan", jsonObj.toString());
            Log.i("JSON Pesan", jsonObj.getString("message"));
            Log.i("JSON Pesan", jsonObj.getString("contactNo"));


            message = jsonObj.getString("message");
            number = jsonObj.getString("contactNo");
            //phoneNumber = jsonObj.getString("adminNo");

            Log.i("Content", message + ", " + number);
            sendSMS(number, message);
            Log.i(message, number);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Toast.makeText(getBaseContext(), "Service Started", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    private void sendSMS(final String contactNo, final String message) {

        try {
            // Get the default instance of the SmsManager
            Log.e("SEND", "SENDING SMS");
            SmsManager sms = SmsManager.getDefault();
            PendingIntent piSent=PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
            smsSentReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    // TODO Auto-generated method stub
                    DBController db;
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Log.e("SEND", "OK");
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Log.e("SEND", "ERROR GF");
                            Toast.makeText(getBaseContext(), "SMSAhok : Sending SMS Failed ! Error Generic Failure", Toast.LENGTH_LONG).show();
                            showNotification(message,getBaseContext(),"Generic Failure",contactNo);
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Log.e("SEND", "ERROR NS");
                            Toast.makeText(getBaseContext(), "SMSAhok : Sending SMS Failed ! Error No Service", Toast.LENGTH_LONG).show();
                            showNotification(message, getBaseContext(), "No Service", contactNo);
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Log.e("SEND", "ERROR NPDU");
                            Toast.makeText(getBaseContext(), "SMSAhok : Sending SMS Failed ! Error Null PDU", Toast.LENGTH_LONG).show();
                            showNotification(message, getBaseContext(), "Null PDU", contactNo);
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Log.e("SEND", "ERROR RO");
                            Toast.makeText(getBaseContext(), "SMSAhok : Sending SMS Failed ! Error Radio Off", Toast.LENGTH_LONG).show();
                            showNotification(message, getBaseContext(), "Radio Off", contactNo);
                            break;
                        default:
                            break;
                    }
                    stopSelf();
                }
            };
            registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
            if(message.length()>160){
                ArrayList<String> parts = sms.divideMessage(message);

                ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

                for (int i = 0; i < parts.size(); i++) {
                    sentIntents.add(PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0));
                }
                sms.sendMultipartTextMessage(contactNo, null, parts, sentIntents, null);
            }else
            sms.sendTextMessage(contactNo, null, message, piSent, null);
        } catch (Exception ex) {
            Log.e("SEND", "SENDING SMS FAILED");
            ex.printStackTrace();
        }
    }

    private void showNotification(String message, Context ctx, String Error, String number) {
        int requestID = (int) System.currentTimeMillis();

        Bitmap icon1 = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Intent notifIntent = new Intent(ctx, NotificationReceive.class);
        notifIntent.putExtra("notificationId",1);
        notifIntent.putExtra("message",message);
        notifIntent.putExtra("contactNo", number);
        notifIntent.setAction("inne.llm.RESEND");
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //sendBroadcast(notifIntent);
        //Create the PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, requestID, notifIntent,0);

        String MyText = "Error";
        //The three parameters are: 1. an icon, 2. a title, 3. time when the notification appears

        String MyNotificationTitle = "Forward Failed";
        String MyNotificationText  = number.substring(0,number.length()-3) +"XXX\n"+ message+"\n Click to retry";

        NotificationCompat.Builder noti = new NotificationCompat.Builder(ctx);
        noti.setContentTitle(MyNotificationTitle);
        noti.setContentText(MyNotificationText);
        noti.setSmallIcon(R.mipmap.ic_launcher);
        noti.setStyle(new NotificationCompat.BigTextStyle().bigText(MyNotificationText));
        noti.setAutoCancel(true);
        noti.setDefaults(Notification.DEFAULT_ALL);
        noti.setLargeIcon(icon1);
        //noti.addAction(R.mipmap.ic_launcher, "inne.llm.RESEND", pendingIntent);
        noti.setTicker("SMS Ahok : Forward Failed ! See details");
        noti.setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(MyNotificationText);
        bigText.setBigContentTitle(MyNotificationTitle);
        bigText.setSummaryText(Error);
        noti.setStyle(bigText);
        noti.setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        manager.notify(m, noti.build());
        stopSelf();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsSentReceiver);
        super.onDestroy();
        Log.i("Destroy", "In onDestroy");
    }
}
