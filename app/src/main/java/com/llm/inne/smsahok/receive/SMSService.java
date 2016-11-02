package com.llm.inne.smsahok.receive;

/**
 * Created by inne on 04/01/16.
 */
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.llm.inne.smsahok.SessionManager;
import com.llm.inne.smsahok.offline.DBController;
import com.llm.inne.smsahok.offline.Message;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by inne on 28/12/15.
 */
public class SMSService extends Service implements Callback {
    DBController db;
    SessionManager user;
    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

    String message, number, id="00", status="online";
    OkHttpClient client = new OkHttpClient();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        message = (String) bundle.get("message");
        number = (String) bundle.get("number");

        Log.i("CONTENT MESSAGES", message +", "+ number );
        user=new SessionManager(this);
        HashMap<String, String> users = user.getUserDetails();
        Log.i("Format 1", users.get(SessionManager.KEY_NUMBER_FORMAT1));

        String newNumb= "0"+number.substring(3);
        Log.e("NEW FORMAT NUMBER", newNumb + " , "+ users.get(SessionManager.KEY_NUMBER_FORMAT1));

        //RequestBody body = RequestBody.create(JSON, "{\"message\":\"" + message + "\", \"senderNumber\":" + "\"" + newNumb + "\",\"phoneNumber\":\"081901596260\"}");
        RequestBody body = RequestBody.create(JSON, "{\"message\":\"" + message + "\", \"senderNumber\":" + "\"" + newNumb + "\",\"phoneNumber\":\"" + users.get(SessionManager.KEY_NUMBER_FORMAT1) + "\"}");
        //RequestBody body = RequestBody.create(JSON, "{\"contentMessage\":\"" + message + "\", \"senderNumber\":" + "\"081901596260\",\"phoneNumber\":\"081901596260\"}");

        Log.i("Tag ", body.toString());
        Request request = new Request.Builder()
                .url("http://128.199.160.191/api/android/store")
                .post(body)
                .build();
        client.newCall(request).enqueue(this);

        return START_NOT_STICKY;
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Log.e("send data", "Failed " + e.toString());
        if(isSDPresent){
            db = new DBController(this);
            db.addMessage(new Message(message, number));
        }
        stopSelf();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if(response.code()==200){
            Log.i("Send Data Receive", String.valueOf(response.code()));
            stopSelf();
        }else{
            Log.i("Send Data Failed", String.valueOf(response.code()));
            if(isSDPresent){
                db = new DBController(this);
                db.addMessage(new Message(message, number));
            }
            response.body().close();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Destroy", "In onDestroy");
    }
}

