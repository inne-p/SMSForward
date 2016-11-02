package com.llm.inne.smsahok;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.llm.inne.smsahok.app.ParseUtils;

import java.util.HashMap;

public class HomeActivity extends Activity implements View.OnClickListener {
    Button remove;
    SessionManager user;
    TextView labelPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        //ParseUtils.registerParse(this);


        labelPhone = (TextView) findViewById(R.id.labelPhoneHome);
        remove = (Button) findViewById(R.id.buttonRemove);
        remove.setOnClickListener(this);
        user=new SessionManager(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            String numberReceive = bundle.getString("number");
            HashMap<String, String> users = user.getUserTempDetails();
            String tempNumber = users.get(SessionManager.KEY_NUMBER_FORMAT2);

            if(numberReceive.equals(tempNumber)){
                Toast.makeText(getApplicationContext(), "Verification Done !", Toast.LENGTH_LONG).show();
                user.createLoginSession(users.get(SessionManager.KEY_NUMBER_FORMAT1));
            }else{
                user.logoutUser();
                Intent h = new Intent(this, LoginActivity.class);
                h.putExtra("number", "unsuccess");
                startActivity(h);
                finish();
            }
        }

        ParseUtils.sub(this);

        HashMap<String, String> users = user.getUserDetails();

        String hide = users.get(SessionManager.KEY_NUMBER_FORMAT2);

        ParseUtils.subscribePhoneNumber(users.get(SessionManager.KEY_NUMBER_FORMAT1));
        Log.i("PhoneNumber Subscribe", users.get(SessionManager.KEY_NUMBER_FORMAT1));

        String hideNumb = hide.substring(0,hide.length()-3);
        labelPhone.setText("Your phone number verification :\n"+hideNumb+"XXX");
        Log.i("SD CARD", String.valueOf(isSDPresent));
        if(!isSDPresent)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getApplicationContext());
            // set title
            alertDialogBuilder.setTitle("Warning");

            // set dialog message
            alertDialogBuilder
                    .setMessage("SD Card not detected ! You must mount SD Card to use offline mode")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            HomeActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

    }

    @Override
    public void onBackPressed() {
        //Display alert message when back button has been pressed
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRemove:
                user.logoutUser();
                ParseUtils.unSub(this);
                Intent h = new Intent(this, LoginActivity.class);
                startActivity(h);
                finish();
                break;
            default:
                break;
        }
    }
}
