package com.llm.inne.smsahok.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;

import com.llm.inne.smsahok.NetworkUtil;
import com.llm.inne.smsahok.SessionManager;
import com.llm.inne.smsahok.offline.DBController;
import com.llm.inne.smsahok.offline.Message;
import com.llm.inne.smsahok.offline.SendService;

/**
 * Created by inne on 13/01/16.
 */
public class CustomSMSReceiver extends BroadcastReceiver {
    DBController db;
    SessionManager user;
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(ACTION)){
            user = new SessionManager(context);
            if(user.isLoggedIn()){
                Bundle bundle = intent.getExtras();
                String strMessageFrom = "", strMessageBody="";
                if (bundle != null){
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++){
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    for (SmsMessage message : messages){

                        strMessageFrom = message.getDisplayOriginatingAddress();
                        strMessageBody += message.getDisplayMessageBody();

                    }
                    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
                    Boolean validNumber = PhoneNumberUtils.isGlobalPhoneNumber(strMessageFrom);
                    Log.i("CONTENT RECEIVE ", strMessageBody);
                    Log.i("VALID PHONE NUMBER ", strMessageFrom + ", "+ validNumber);
                    //System.out.println(strMessageBody);
                    int a= NetworkUtil.getConnectivityStatus(context);
                    Log.i("STATUS NETWORK", String.valueOf(a));
                    if(a==0){
                        if(!strMessageBody.contains("SMSAhok")){
                            Log.i("SD CARD", String.valueOf(isSDPresent));
                            if(isSDPresent)
                            {
                                db = new DBController(context);
                                db.addMessage(new Message(strMessageBody, strMessageFrom));
                            }
                        }
                    }
                    else{
                        if(!strMessageBody.contains("SMSAhok") && validNumber==true && strMessageFrom.length()>7){
                            context.startService(new Intent(context, SMSService.class).putExtra("message", strMessageBody).putExtra("number", strMessageFrom));
                            if(isSDPresent)
                            {
                                db=new DBController(context);
                                int check = db.countMessage();
                                Log.i("Check Message", String.valueOf(check));
                                if(check>0)
                                {
                                    context.startService(new Intent(context, SendService.class));
                                }
                            }
                        }
                    }
                }
            }

        }
    }

}
