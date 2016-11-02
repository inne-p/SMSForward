package com.llm.inne.smsahok;

/**
 * Created by Admin on 27/05/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
        import android.telephony.TelephonyManager;
        import android.util.Log;

        import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences pref1;
    // Editor for Shared preferences
    Editor editor, editor1;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidSMSAhok";
    private static final String PREF_NAME1 = "AndroidSMSAhokTemp";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NUMBER_FORMAT1 = "number1";
    public static final String KEY_NUMBER_FORMAT2 = "number2";

    // Email address (make variable public to access from outside)
    //public static final String KEY_ = "pass";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        pref1 = _context.getSharedPreferences(PREF_NAME1, PRIVATE_MODE);
        editor1 = pref1.edit();
        editor = pref.edit();
    }

    public void createLoginSession(String number){
        editor1.clear();
        editor1.commit();
        String newNumb= "+62"+number.substring(1);
        Log.e("NEW FORMAT NUMBER", newNumb);

        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref


        editor.putString(KEY_NUMBER_FORMAT1, number);
        editor.putString(KEY_NUMBER_FORMAT2, newNumb);

        // Storing email in pref
       // editor.putString(KEY_PASS, pass);

        // commit changes
        editor.commit();
    }

    public void createTempLoginSession(String number){
        String newNumb="";

            newNumb = "+62" + number.substring(1);
        Log.e("NEW FORMAT NUMBER", newNumb);
        // Storing login value as TRUE
        editor1.putBoolean(IS_LOGIN, true);

        // Storing name in pref

        editor1.putString(KEY_NUMBER_FORMAT1, number);
        editor1.putString(KEY_NUMBER_FORMAT2, newNumb);


        // Storing email in pref
        // editor.putString(KEY_PASS, pass);

        // commit changes
        editor1.commit();
    }

    public HashMap<String, String> getUserTempDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(KEY_NUMBER_FORMAT1, pref1.getString(KEY_NUMBER_FORMAT1, null));
        user.put(KEY_NUMBER_FORMAT2, pref1.getString(KEY_NUMBER_FORMAT2, null));
        // user email id
        // user.put(KEY_PASS, pref.getString(KEY_PASS, null));

        // return user
        return user;
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(KEY_NUMBER_FORMAT1, pref.getString(KEY_NUMBER_FORMAT1, null));
        user.put(KEY_NUMBER_FORMAT2, pref.getString(KEY_NUMBER_FORMAT2, null));
        // user email id
       // user.put(KEY_PASS, pref.getString(KEY_PASS, null));

        // return user
        return user;
    }

    public void logoutUser(){
        //ParsePush.unsubscribeInBackground(getString(R.string.shirt_channel));
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();


    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}