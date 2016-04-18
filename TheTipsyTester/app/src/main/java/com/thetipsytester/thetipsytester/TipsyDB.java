package com.thetipsytester.thetipsytester;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class TipsyDB extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TipsyDB";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_TESTS = "tests";
    private static final String TABLE_SCORES = "scores";

    // Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_TEST = "test";

    // USERS Table - column names
    private static final String KEY_NAME = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_WEIGHT = "weight";

    // TESTS Table - column names
    private static final String KEY_UT04 = "ut04";
    private static final String KEY_UT08 = "ut08";
    private static final String KEY_UT12 = "ut12";
    private static final String KEY_UT16 = "ut16";
    private static final String KEY_UT20 = "ut20";
    private static final String KEY_AB20 = "ab20";

    // SCORES Table - column names
    private static final String KEY_BEST = "best";
    private static final String KEY_WORST = "worst";

    // Table Create Statements
    // USERS table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_GENDER + " TEXT," + KEY_WEIGHT
            + " INTEGER" + ")";

    // TESTS table create statement
    private static final String CREATE_TABLE_TESTS = "CREATE TABLE " + TABLE_TESTS
            + "(" + KEY_ID + " INTEGER NOT NULL," + KEY_TEST + " TEXT NOT NULL,"
            + KEY_UT04 + " INTEGER," + KEY_UT08 + " INTEGER," + KEY_UT12 + " INTEGER,"
            + KEY_UT16 + " INTEGER," + KEY_UT20 + " INTEGER," + KEY_AB20 + " INTEGER,"
            + "PRIMARY KEY (" + KEY_ID + ", " + KEY_TEST + ")"
            + "FOREIGN KEY (" + KEY_ID + ") REFERENCES users(" + KEY_ID + "))";

    // SCORES table create statement
    private static final String CREATE_TABLE_SCORES = "CREATE TABLE "
            + TABLE_SCORES + "(" + KEY_ID + " INTEGER NOT NULL,"
            + KEY_TEST + " TEXT NOT NULL," + KEY_BEST + " INTEGER,"
            + KEY_WORST + " INTEGER," + "PRIMARY KEY (" + KEY_ID + ", " + KEY_TEST + ")"
            + "FOREIGN KEY (" + KEY_ID + ") REFERENCES users(" + KEY_ID + "))";

    public TipsyDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_TESTS);
        db.execSQL(CREATE_TABLE_SCORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);

        // create new tables
        onCreate(db);
    }
}
