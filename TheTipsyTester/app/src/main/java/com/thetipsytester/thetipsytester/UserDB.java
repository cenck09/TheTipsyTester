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
    private static final String DATABASE_NAME = "joke.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE jokes (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT, " +
                    "setup TEXT, " +
                    "punchline TEXT," +
                    "liked TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS jokes";

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

        String[] names = appContext.getResources().getStringArray(R.array.UserName);
        String[] genders = appContext.getResources().getStringArray(R.array.UserGender);
        String[] weights = appContext.getResources().getStringArray(R.array.UserWeight);

        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("liked","?");
        for (int i = 0; i < names.length; i++) {
            values.put("name", names[i]);
            values.put("gender", genders[i]);
            values.put("weight", weights[i]);
            //values.put("liked", "?");
            db.insert("user", null, values);
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
