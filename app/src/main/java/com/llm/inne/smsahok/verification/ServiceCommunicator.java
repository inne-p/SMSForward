package com.llm.inne.smsahok.verification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import com.llm.inne.smsahok.HomeActivity;
import com.llm.inne.smsahok.LoginActivity;

/**
 * Created by inne on 15/01/16.
 */
public class ServiceCommunicator extends Service {
    BroadcastReceiver brSms;
    private IntentFilter mIntentFilter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("VERIFICATION SERVICE", "STARTED");
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        brSms = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                    Bundle bundle = intent.getExtras();
                    if (bundle != null){
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        SmsMessage[] messages = new SmsMessage[pdus.length];
                        for (int i = 0; i < pdus.length; i++){
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        for (SmsMessage message : messages){

                            String strMessageFrom = message.getDisplayOriginatingAddress();
                            String strMessageBody = message.getDisplayMessageBody();
                            Log.i("NUMBER", strMessageFrom);
                            if(strMessageBody.contains("SMSAhok")){
                                Log.i("MESSAGE CONTAIN", "SMSAhok");
                                context.startActivity(new Intent(context, HomeActivity.class).putExtra("number", strMessageFrom).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                            }else {
                                context.startActivity(new Intent(context, LoginActivity.class).putExtra("number", "unsuccess").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                            stopSelf();
                        }
                    }
                }

            }
        };

        registerReceiver(brSms, mIntentFilter);
    }

    @Override
    public void onStart(Intent intent, int startid) {
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(brSms);
        super.onDestroy();
        Log.e("VERIFICATION SERVICE", "DESTROY");
    }
}
