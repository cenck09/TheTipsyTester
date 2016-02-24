package com.example.nick.sqlite_database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    SQLiteDatabase theDB;
    long currentRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserDB.getInstance(this).getWritableDatabase(new UserDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase theDB) {
                // Will this.theDB work?
                MainActivity.this.theDB = theDB;
            }
        });
    }


    /*
    public void btnAddClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put("setup", ((TextView) findViewById(R.id.txtNewSetup)).getText().toString());
            values.put("punchline", ((TextView) findViewById(R.id.txtNewPunchline)).getText().toString());

            long newRowId = theDB.insert("jokes", null, values);
        }
    }
    */


    /*
    public void btnSearchClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            String[] columns = {"_id", "setup", "punchline"};

            String selection = "_id = ?";
            String[] selectionArgs = new String[] {((TextView) findViewById(R.id.txtSearchID)).getText().toString()};

            Cursor c = theDB.query("jokes", columns, selection, selectionArgs, null, null, null);

            if (c.moveToFirst()) {
                currentRow = c.getLong(c.getColumnIndexOrThrow("_id"));
                ((TextView) findViewById(R.id.txtEditSetup)).setText(c.getString(c.getColumnIndexOrThrow("setup")));
                ((TextView) findViewById(R.id.txtEditPunchline)).setText(c.getString(c.getColumnIndexOrThrow("punchline")));

                changeEditFieldVisibility(View.VISIBLE);
            }
            else {
                changeEditFieldVisibility(View.GONE);
            }
            c.close();
        }
    }
    */

    /*
    private void changeEditFieldVisibility(int visibility) {
        findViewById(R.id.txtEditSetup).setVisibility(visibility);
        findViewById(R.id.txtEditPunchline).setVisibility(visibility);
        findViewById(R.id.btnUpdate).setVisibility(visibility);
        findViewById(R.id.btnDelete).setVisibility(visibility);
    }
    */


    /*
    public void btnUpdateClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put("setup", ((TextView) findViewById(R.id.txtEditSetup)).getText().toString());
            values.put("punchline", ((TextView) findViewById(R.id.txtEditPunchline)).getText().toString());

            String selection = "_id = " + currentRow;

            theDB.update("jokes",values,selection,null);
        }
    }
    */

    public void btnDeleteClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            String selection = "_id = " + currentRow;

            theDB.delete("jokes",selection,null);
        }
    }


    public void btnRefreshClick(View view) {
        StringBuffer sb = new StringBuffer();
        String[] columns = {"_id", "name", "age", "weight", "gender"};

        Cursor c = theDB.query("user", columns, null, null, null, null, "_id");

        while (c.moveToNext()) {
            sb.append("id: " + c.getLong(c.getColumnIndexOrThrow("_id")) + "\n");
            sb.append(c.getString(c.getColumnIndexOrThrow("setup")));
            sb.append("\n");
            sb.append(c.getString(c.getColumnIndexOrThrow("punchline")));
            sb.append("---------------------------------------------------------------\n");
        }
        ((TextView) findViewById(R.id.lblResults)).setText(sb);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (theDB != null) theDB.close();
    }

}

