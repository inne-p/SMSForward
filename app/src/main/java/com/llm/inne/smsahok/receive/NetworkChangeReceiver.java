package com.llm.inne.smsahok.receive;

/**
 * Created by inne on 05/01/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.llm.inne.smsahok.NetworkUtil;
import com.llm.inne.smsahok.offline.DBController;
import com.llm.inne.smsahok.offline.Message;
import com.llm.inne.smsahok.offline.SendService;

import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);
        int a = NetworkUtil.getConnectivityStatus(context);
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();

        if (a == 1 || a == 2) {
            DBController db = new DBController(context);
            // get all books
            int c = db.countMessage();
            if (c > 0) {
                context.startService(new Intent(context, SendService.class));
                Log.d("SERVICE", "START");
                //context.startService(new Intent(context, SendService.class));

            }
        }
    }
}