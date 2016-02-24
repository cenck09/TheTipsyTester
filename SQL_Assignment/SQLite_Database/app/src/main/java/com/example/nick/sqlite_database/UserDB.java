package com.example.nick.sqlite_database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class UserDB extends SQLiteOpenHelper{


    public interface onDBReadyListener{
        public void onDBReady(SQLiteDatabase theDB);
    }


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user.db";

    private static UserDB theDB;


    private UserDB(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    public static synchronized UserDB getInstance(Context context){
        if(theDB==null)
            theDB = new UserDB(context.getApplicationContext());
        return theDB;
    }



    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE user (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "age INTEGER, " +
                    "weight INTEGER, " +
                    "gender TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF USER EXISTS";

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }



    public void getWritableDatabase(onDBReadyListener listener){
        new OpenDBAsyncTask().execute(listener);

    }

    private class OpenDBAsyncTask extends AsyncTask<onDBReadyListener, Void, SQLiteDatabase> {
        onDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(onDBReadyListener... params ){
            listener = params[0];
            return theDB.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase theDB){
            listener.onDBReady(theDB);
        }

    }



}
