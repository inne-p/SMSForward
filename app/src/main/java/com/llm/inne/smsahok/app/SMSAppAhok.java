package com.llm.inne.smsahok.app;


import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.llm.inne.smsahok.SessionManager;
import com.parse.ParseAnalytics;


public class SMSAppAhok extends Application {
    SessionManager user;
    private static SMSAppAhok mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        ParseUtils.registerParse(this);

    }


    public static synchronized SMSAppAhok getInstance() {
        return mInstance;
    }
}
