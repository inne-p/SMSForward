package com.llm.inne.smsahok.forward;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.llm.inne.smsahok.SessionManager;
import com.parse.ParseBroadcastReceiver;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by inne on 13/01/16.
 */
public class CustomPushReceiver extends ParsePushBroadcastReceiver {
    private final String TAG = CustomPushReceiver.class.getSimpleName();

    public CustomPushReceiver() {
        super();
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.e(TAG, "Push Received: " + json);


            parsePushJson(context, json);

        } catch (JSONException e) {
            Log.e(TAG, "Push Message json exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    /**
     * Parses the push notification json
     *
     * @param context
     * @param json
     */
    private void parsePushJson(Context context, JSONObject json) {
        SessionManager user = new SessionManager(context);
        HashMap<String, String> users = user.getUserDetails();

        try {
            Log.i("JSON Pesan", json.getString("adminNo"));
            if(json.getString("adminNo").equals(users.get(SessionManager.KEY_NUMBER_FORMAT1))){
                context.startService(new Intent(context, SMSForwardService.class).putExtra("JSON", json.toString()));
                Log.e(TAG, "Success");
            }else{
                Log.e(TAG, "NOT ADMIN");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
