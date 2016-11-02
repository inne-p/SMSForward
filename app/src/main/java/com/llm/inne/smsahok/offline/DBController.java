package com.llm.inne.smsahok.offline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by inne on 05/01/16.
 */
public class DBController extends SQLiteOpenHelper {
    private final Context myContext;
    private static String DB_PATH ="";
    private static final String[] COLUMNS = {"messageID","messageContent","senderNumber","created"};

    public DBController(Context applicationcontext) {
        super(applicationcontext, "db_message", null, 1);
        this.myContext = applicationcontext;
    }

    public void onCreate(SQLiteDatabase database) {

        String query;
        query = "CREATE TABLE  IF NOT EXISTS message ( " +
                "messageID INTEGER PRIMARY KEY, " +
                "senderNumber TEXT, " +
                "messageContent TEXT," +
                "created DATETIME DEFAULT CURRENT_TIMESTAMP)";
        database.execSQL(query);

        query = "CREATE TABLE  IF NOT EXISTS messageForward ( " +
                "messageID INTEGER PRIMARY KEY, " +
                "destNumber TEXT, " +
                "messageContent TEXT," +
                "created DATETIME DEFAULT CURRENT_TIMESTAMP)";
        database.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {

        onCreate(database);
    }


    public void addMessage(Message  message){
        //for logging
        Log.i("ADD MESSAGE TO DB", message.getMessage());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("messageContent", message.getMessage());
        values.put("senderNumber", message.getNumber());

        // 3. insert
        db.insert("message", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void addMessageForward(Message  message){
        //for logging
        Log.i("ADD MESSAGE TO DB", message.getMessage());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("messageContent", message.getMessage());
        values.put("destNumber", message.getNumber());

        // 3. insert
        db.insert("messageForward", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Message getMessage(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query("message", // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        Message message = new Message();
        message.setId(Integer.parseInt(cursor.getString(0)));
        message.setNumber(cursor.getString(1));
        message.setMessage(cursor.getString(2));

        //log
        Log.d("getMessage(" + id + ")", message.toString());

        // 5. return book
        return message;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new LinkedList<Message>();

        // 1. build the query
        String query = "SELECT  * FROM message";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Message message = null;
        if (cursor.moveToFirst()) {
            do {
                message = new Message();
                message.setId(Integer.parseInt(cursor.getString(0)));
                message.setNumber(cursor.getString(1));
                message.setMessage(cursor.getString(2));
                message.setDate(cursor.getString(3));
                Log.i("DATE DB", cursor.getString(3));
                // Add book to books
                messages.add(message);
            } while (cursor.moveToNext());
        }

        Log.d("ALL MESSAGES", messages.get(0).toString());


        return messages;
    }

    public List<Message> getAllForwardMessages() {
        List<Message> messages = new LinkedList<Message>();

        // 1. build the query
        String query = "SELECT  * FROM messageForward";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Message message = null;
        if (cursor.moveToFirst()) {
            do {
                message = new Message();
                message.setId(Integer.parseInt(cursor.getString(0)));
                message.setNumber(cursor.getString(1));
                message.setMessage(cursor.getString(2));
                message.setDate(cursor.getString(3));
                Log.i("DATE DB", cursor.getString(3));
                // Add book to books
                messages.add(message);
            } while (cursor.moveToNext());
        }

        Log.d("ALL FORWARD MESSAGES", messages.get(0).toString());

        return messages;
    }


    public void deleteMessageId(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("message", //table name
                "messageID"+" = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        //log
        Log.e("deleteMessage", String.valueOf(id));

    }

    public void deleteForwardMessageId(int id) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete("messageForward", //table name
                "messageID"+" = ?",  // selections
                new String[] { String.valueOf(id) }); //selections args

        // 3. close
        //log
        Log.e("deleteForwardMessage", String.valueOf(id));

    }

    public int countMessage(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from message", null);
        mCount.moveToFirst();
        int a = mCount.getInt(0);
        mCount.close();
        Log.d("Record", String.valueOf(a));
        return a;
    }

    public int countForwardMessage(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from messageForward", null);
        mCount.moveToFirst();
        int a = mCount.getInt(0);
        mCount.close();
        Log.d("Record Forward", String.valueOf(a));
        return a;
    }
}
