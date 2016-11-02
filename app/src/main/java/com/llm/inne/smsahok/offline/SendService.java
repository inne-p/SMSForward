package com.llm.inne.smsahok.offline;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.llm.inne.smsahok.SessionManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.framed.FrameReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by inne on 05/01/16.
 */
public class SendService extends Service  {
    DBController db;
    SessionManager user;
    int i=0, b=1;
    List<Message> list;
    OkHttpClient client = new OkHttpClient();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = new DBController(this);

        list = db.getAllMessages();
        b=list.size();
        Log.i("Size",String.valueOf(b));
        for(i=0;i<list.size();i++){
            Log.i("CONTENT MESSAGE", "SEND SERVICE"+list.get(i).getMessage());

            String message = list.get(i).getMessage();
            String number = list.get(i).getNumber();
            int id = list.get(i).getId();
            user=new SessionManager(this);
            HashMap<String, String> users = user.getUserDetails();

            Log.i("Content DATE-"+i,list.get(i).getDate());
            RequestBody body = RequestBody.create(JSON, "{\"message\":\"" + message + "\", \"senderNumber\":" + "\"" + number + "\",\"phoneNumber\":\"" + users.get(SessionManager.KEY_NUMBER_FORMAT1) + "\"}");
            //RequestBody body = RequestBody.create(JSON, "{\"contentMessage\":\"" + message + "\", \"senderNumber\":" + "\"081901506260\",\"phoneNumber\":\"081901506260\"}");

            Log.i("Tag ", body.toString());
            Request request = new Request.Builder()
                    .url("http://128.199.160.191/api/android/store")
                    .post(body)
                    .build();
            call(request, id);
            //client.newCall(request).enqueue(this);
            Log.i("J", String.valueOf(i));

        }
        stopSelf();
        return START_NOT_STICKY;
    }


    private void call(Request request, final int id) {


        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("send data", "Failed"+ e.toString());

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if(response.code()==200){
                    Log.i("Send Data Send Receive", String.valueOf(response.code()));
                    db.deleteMessageId(id);
                }else{
                    Log.i("Send Data Failed", String.valueOf(response.code()));
                }
                response.body().close();
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        db.close();
        Log.i("Destroy", "In onDestroy");
    }
}
