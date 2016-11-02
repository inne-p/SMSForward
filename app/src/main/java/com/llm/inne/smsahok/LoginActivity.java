package com.llm.inne.smsahok;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.llm.inne.smsahok.app.ParseUtils;
import com.llm.inne.smsahok.offline.DBController;
import com.llm.inne.smsahok.verification.ServiceCommunicator;
import com.llm.inne.smsahok.verification.Verify;

public class LoginActivity extends Activity implements View.OnClickListener {
    SessionManager user;
    Button submit, remove;
    TextView phoneNumb;
    EditText inputNumb;
    BroadcastReceiver smsSentReceiver, smsDelivered;
    String number;Boolean receiver=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        user= new SessionManager(this);

        submit = (Button) findViewById(R.id.buttonSubmit);
        phoneNumb = (TextView) findViewById(R.id.labelPhoneHome);
        inputNumb = (EditText) findViewById(R.id.inputNumber);

        submit.setOnClickListener(this);

        //ParseUtils.registerParse(this);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            Toast.makeText(getApplicationContext(), "Insert valid phone number !", Toast.LENGTH_LONG).show();
        }
        Boolean cek=user.isLoggedIn();
        if(cek){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);

            finish();
        }
        // inform the Parse Cloud that it is ready for notifications
        ParseUtils.verifyParseConfiguration(this);
        ParseUtils.unSub(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSubmit:

                SmsManager sms = SmsManager.getDefault();
                if(inputNumb.getText().toString().isEmpty() || inputNumb.getText().toString().length() >15){
                    Toast.makeText(getApplicationContext(), "Insert valid phone number !", Toast.LENGTH_LONG).show();
                }else {
                    number = inputNumb.getText().toString();
                    user.createTempLoginSession(number);
                    try {
                        PendingIntent sentPI;
                        String SENT = "SMS_SENT";
                        String  DELIVERED = "SMS_DELIVERED";
                        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);
                        sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);
                        smsSentReceiver=new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context arg0, Intent arg1) {
                                // TODO Auto-generated method stub
                                switch (getResultCode()) {
                                    case Activity.RESULT_OK:
                                        Log.e("SEND", "OK");
                                        Toast.makeText(getBaseContext(), "Sending SMS Success !", Toast.LENGTH_LONG).show();
                                        getApplicationContext().startService(new Intent(getApplicationContext(), ServiceCommunicator.class));
                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                        Log.e("SEND", "ERROR GF");
                                        Toast.makeText(getBaseContext(), "Sending SMS Failed ! Error Generic Failure", Toast.LENGTH_LONG).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        Log.e("SEND", "ERROR NS");
                                        Toast.makeText(getBaseContext(), "Sending SMS Failed ! No Service", Toast.LENGTH_LONG).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        Log.e("SEND", "ERROR NPDU");
                                        Toast.makeText(getBaseContext(), "Sending SMS Failed ! Null PDU", Toast.LENGTH_LONG).show();
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        Log.e("SEND", "ERROR RO");
                                        Toast.makeText(getBaseContext(), "Sending SMS Failed ! Radio Off", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        };
                        smsDelivered = new BroadcastReceiver(){
                            @Override
                            public void onReceive(Context  arg0, Intent arg1) {
                                switch (getResultCode())
                                {
                                    case Activity.RESULT_OK:
                                        Toast.makeText(getBaseContext(), "SMS delivered",
                                                Toast.LENGTH_SHORT).show();
                                        Log.e("SEND", "Delivered");
                                        break;
                                    case Activity.RESULT_CANCELED:
                                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                                Toast.LENGTH_SHORT).show();
                                        Log.e("SEND", "Cancelled");
                                        break;
                                }
                            }
                        };
                        registerReceiver(smsSentReceiver, new IntentFilter("SMS_SENT"));
                        registerReceiver(smsDelivered, new IntentFilter("SMS_DELIVERED"));
                        receiver = true;
                        sms.sendTextMessage(inputNumb.getText().toString(), null, "SMSAhok verification", sentPI, deliveredPI);

                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(), "Failed Send SMS !", Toast.LENGTH_LONG).show();
                    }

                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        if(receiver){
            unregisterReceiver(smsSentReceiver);
            unregisterReceiver(smsDelivered);
        }
        super.onDestroy();
        Log.i("Destroy", "In onDestroy");
    }
}
