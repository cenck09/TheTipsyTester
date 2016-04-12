package com.thetipsytester.thetipsytester;

/**
 * Created by Nick on 3/30/2016.
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


@SuppressWarnings("ConstantConditions")
public class UserContentProvider extends ContentProvider {

    private UserDB theDB;

    private static final String AUTHORITY = "com.thetipsytester.thetipsytester.users";
    private static final String BASE_PATH = "users";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +
            BASE_PATH);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int USERS = 1;
    private static final int USERS_ID = 2;
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, USERS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", USERS_ID);
    }

    @Override
    public boolean onCreate() {
        theDB = UserDB.getInstance(getContext());
        return true;
    }



    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;
        SQLiteDatabase db = theDB.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case USERS:
                id = db.insert("users", null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = theDB.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case USERS:
                cursor = db.query("users", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case USERS_ID:
                cursor = db.query("users", projection,
                        appendIdToSelection(selection, uri.getLastPathSegment()),
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        SQLiteDatabase db = theDB.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case USERS:
                count = db.update("users", values, selection, selectionArgs);
                break;
            case USERS_ID:
                count = db.update("users", values,
                        appendIdToSelection(selection, uri.getLastPathSegment()),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = theDB.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case USERS:
                count = db.delete("users", selection, selectionArgs);
                break;
            case USERS_ID:
                count = db.delete("users",
                        appendIdToSelection(selection, uri.getLastPathSegment()),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    private String appendIdToSelection(String selection, String sId) {
        int id = Integer.valueOf(sId);

        if (selection == null || selection.trim().equals(""))
            return "_ID = " + id;
        else
            return selection + " AND _ID = " + id;
    }
}
