package com.llm.inne.smsahok.verification;

import android.util.Log;

/**
 * Created by inne on 15/01/16.
 */
public class Verify {
    public static String number="";

    public Verify(){
    }

    public void setNumber(String number){
        this.number = number;
        Log.e("NEW NUMBER VERIFY", this.number);
    }
    public String getNumber(){
        String newNumb= "+62"+number.substring(1);
        Log.e("NEW FORMAT NUMBER", newNumb);
        return newNumb;
    }

    public String getOriNumber(){
        return this.number;
    }
}
