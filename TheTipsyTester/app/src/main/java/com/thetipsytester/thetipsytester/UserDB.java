package com.thetipsytester.thetipsytester;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class UserDB extends SQLiteOpenHelper {

    public interface OnDBReadyListener {
        public void onDBReady(SQLiteDatabase theDB);
    }

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "users.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE users (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT, " +
                    "gender TEXT, " +
                    "weight TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS users";

    private static UserDB theDb;
    private Context appContext;

    private UserDB(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        appContext = context.getApplicationContext();
    }

    public static synchronized UserDB getInstance(Context context) {
        if (theDb == null) {
            theDb = new UserDB(context.getApplicationContext());
        }

        return theDb;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

        String[] name = appContext.getResources().getStringArray(R.array.name);
        String[] gender = appContext.getResources().getStringArray(R.array.gender);
        String[] weight = appContext.getResources().getStringArray(R.array.weight);

        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (int i = 0; i < name.length; i++) {
            values.put("name", name[i]);
            values.put("gender", gender[i]);
            values.put("weight", weight[i]);
            db.insert("users", null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
