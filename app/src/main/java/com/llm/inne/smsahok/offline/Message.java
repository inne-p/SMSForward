package com.llm.inne.smsahok.offline;

import android.util.Log;

/**
 * Created by inne on 05/01/16.
 */
public class Message {
        int id;
        String message, number, date;

        // constructors
        public Message() {

        }

        public Message(String message, String number) {
            Log.i(message,number);
            this.message = message;
            this.number = number;
        }

        public Message(int id, String message, String number) {
            this.id = id;
            this.message = message;
            this.number = number;
        }

        // setter
        public void setId(int id) {
            this.id = id;
        }

        public void setMessage(String tag_name) {
            this.message = tag_name;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setDate(String date) {
            Log.i("Date SET",date);
        this.date = date;
        }
        // getter
        public int getId() {
            return this.id;
        }

        public String getMessage() {
            return this.message;
        }

        public String getDate() {
            Log.i("Date GET",date);
            return this.date;
        }

    public String getNumber() {
            return this.number;
        }

}
