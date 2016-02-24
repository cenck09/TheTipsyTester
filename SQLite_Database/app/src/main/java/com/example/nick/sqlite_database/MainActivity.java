package com.example.nick.sqlite_database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
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

        UserDB.getInstance(this).getWritableDatabase(new UserDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase theDB) {
                // Will this.theDB work?
                MainActivity.this.theDB = theDB;
            }
        });
    }



    public void btnAddClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put("name", ((TextView) findViewById(R.id.editText)).getText().toString());
            values.put("age", ((TextView) findViewById(R.id.editText2)).getText().toString());
            values.put("weight", ((TextView) findViewById(R.id.editText3)).getText().toString());
            //values.put("gender", ((TextView) findViewById(R.id.r)).getText().toString());

            RadioButton buttonFemale = (RadioButton) this.findViewById(R.id.radioFemale);

            if(buttonFemale.isChecked()){
                //female is checked
                values.put("gender", "female");
            }else{
                //male is checked
                values.put("gender", "male");
            }
            long newRowId = theDB.insert("user", null, values);
        }
    }



    public void btnSearchClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            String[] columns = {"_id", "name", "age", "weight", "gender"};

            String selection = "_id = ?";
            String[] selectionArgs = new String[] {((TextView) findViewById(R.id.editText4)).getText().toString()};

            Cursor c = theDB.query("user", columns, selection, selectionArgs, null, null, null);

            if (c.moveToFirst()) {
                currentRow = c.getLong(c.getColumnIndexOrThrow("_id"));
                ((TextView) findViewById(R.id.txtEditName)).setText(c.getString(c.getColumnIndexOrThrow("name")));
                ((TextView) findViewById(R.id.txtEditAge)).setText(c.getString(c.getColumnIndexOrThrow("age")));
                ((TextView) findViewById(R.id.txtEditWeight)).setText(c.getString(c.getColumnIndexOrThrow("weight")));

                RadioButton buttonFemale = (RadioButton) this.findViewById(R.id.radioFemaleEdit);
                RadioButton buttonMale = (RadioButton) this.findViewById(R.id.radioMaleEdit);

                if(c.getString(c.getColumnIndexOrThrow("gender")).equals("female")){
                    buttonFemale.setChecked(true);
                }else{
                    buttonMale.setChecked(true);
                }


                changeEditFieldVisibility(View.VISIBLE);
            }
            else {
                changeEditFieldVisibility(View.GONE);
            }
            c.close();
        }
    }



    private void changeEditFieldVisibility(int visibility) {
        findViewById(R.id.txtEditName).setVisibility(visibility);
        findViewById(R.id.txtEditAge).setVisibility(visibility);
        findViewById(R.id.txtEditWeight).setVisibility(visibility);
        findViewById(R.id.radioGroupEdit).setVisibility(visibility);
        findViewById(R.id.btnUpdate).setVisibility(visibility);
        findViewById(R.id.btnDelete).setVisibility(visibility);
    }



    public void btnUpdateClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            values.put("name", ((TextView) findViewById(R.id.txtEditName)).getText().toString());
            values.put("age", ((TextView) findViewById(R.id.txtEditAge)).getText().toString());
            values.put("weight", ((TextView) findViewById(R.id.txtEditWeight)).getText().toString());

            RadioButton buttonFemale = (RadioButton) this.findViewById(R.id.radioFemaleEdit);


            if(buttonFemale.isChecked()){
                values.put("gender", "female");
            }else{
                values.put("gender", "male");
            }



            String selection = "_id = " + currentRow;

            theDB.update("user",values,selection,null);
        }
    }


    public void btnDeleteClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            String selection = "_id = " + currentRow;

            theDB.delete("user",selection,null);
        }
    }


    public void btnRefreshClick(View view) {
        StringBuffer sb = new StringBuffer();
        String[] columns = {"_id", "name", "age", "weight", "gender"};

        Cursor c = theDB.query("user", columns, null, null, null, null, "_id");

        while (c.moveToNext()) {
            sb.append("id: " + c.getLong(c.getColumnIndexOrThrow("_id")) + "\n");
            sb.append(c.getString(c.getColumnIndexOrThrow("name")));
            sb.append("\n");
            sb.append(c.getString(c.getColumnIndexOrThrow("age")));
            sb.append("\n");
            sb.append(c.getString(c.getColumnIndexOrThrow("weight")));
            sb.append("\n");
            sb.append(c.getString(c.getColumnIndexOrThrow("gender")));
            sb.append("\n");
            sb.append("------------------------------------------------------------\n");
        }
        ((TextView) findViewById(R.id.lblResults)).setText(sb);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (theDB != null) theDB.close();
    }

}

