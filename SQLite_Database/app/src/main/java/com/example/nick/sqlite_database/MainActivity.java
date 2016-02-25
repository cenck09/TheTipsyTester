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
            TextView nameField = (TextView) findViewById(R.id.editText);
            TextView ageField = (TextView) findViewById(R.id.editText2);
            TextView weightField = (TextView) findViewById(R.id.editText3);

            String name = nameField.getText().toString();

            values.put("name", name);
            values.put("age", ageField.getText().toString());
            values.put("weight", weightField.getText().toString());

            RadioButton buttonFemale = (RadioButton) this.findViewById(R.id.radioFemale);

            if(buttonFemale.isChecked()){
                //female is checked
                values.put("gender", "female");
            }else{
                //male is checked
                values.put("gender", "male");
            }
            long newRowId = theDB.insert("user", null, values);
            this.updateTable();
            Toast.makeText(this, "You have added "+ name, Toast.LENGTH_SHORT).show();
            this.changeEditFieldVisibility(View.GONE);
            this.clearFields();
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
                ((TextView) findViewById(R.id.editText)).setText(c.getString(c.getColumnIndexOrThrow("name")));
                ((TextView) findViewById(R.id.editText2)).setText(c.getString(c.getColumnIndexOrThrow("age")));
                ((TextView) findViewById(R.id.editText3)).setText(c.getString(c.getColumnIndexOrThrow("weight")));

                RadioButton buttonFemale = (RadioButton) this.findViewById(R.id.radioFemale);
                RadioButton buttonMale = (RadioButton) this.findViewById(R.id.radioMale);

                if(c.getString(c.getColumnIndexOrThrow("gender")).equals("female")){
                    buttonFemale.setChecked(true);
                }else{
                    buttonMale.setChecked(true);
                }


                changeEditFieldVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(this, "Failed to find ID.", Toast.LENGTH_SHORT).show();
                changeEditFieldVisibility(View.GONE);
            }
            c.close();
        }
    }



    private void changeEditFieldVisibility(int visibility) {
     //   findViewById(R.id.txtEditName).setVisibility(visibility);
     //   findViewById(R.id.txtEditAge).setVisibility(visibility);
     //   findViewById(R.id.txtEditWeight).setVisibility(visibility);
     //   findViewById(R.id.radioGroupEdit).setVisibility(visibility);
        if(visibility == View.GONE){
            findViewById(R.id.button3).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.button3).setVisibility(View.GONE);
        }
        findViewById(R.id.btnUpdate).setVisibility(visibility);
        findViewById(R.id.btnDelete).setVisibility(visibility);
    }



    public void btnUpdateClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            ContentValues values = new ContentValues();
            TextView nameField = (TextView) findViewById(R.id.editText);
            TextView ageField = (TextView) findViewById(R.id.editText2);
            TextView weightField = (TextView) findViewById(R.id.editText3);

            String name = nameField.getText().toString();

            values.put("name", name);
            values.put("age", ageField.getText().toString());
            values.put("weight", weightField.getText().toString());

           this.clearFields();

            RadioButton buttonFemale = (RadioButton) this.findViewById(R.id.radioFemale);


            if(buttonFemale.isChecked()){
                values.put("gender", "female");
            }else{
                values.put("gender", "male");
            }


            changeEditFieldVisibility(View.GONE);

            String selection = "_id = " + currentRow;

            theDB.update("user",values,selection,null);
            Toast.makeText(this, "You have updated "+ name, Toast.LENGTH_SHORT).show();
            this.updateTable();
        }
    }
    public void clearFields(){
        ContentValues values = new ContentValues();
        TextView nameField = (TextView) findViewById(R.id.editText);
        TextView ageField = (TextView) findViewById(R.id.editText2);
        TextView weightField = (TextView) findViewById(R.id.editText3);
        TextView searchField = (TextView) findViewById(R.id.editText4);

        String name = nameField.getText().toString();

        values.put("name", name);
        values.put("age", ageField.getText().toString());
        values.put("weight", weightField.getText().toString());

        searchField.setText("");
        nameField.setText("");
        ageField.setText("");
        weightField.setText("");

    }

    public void btnDeleteClick(View view) {
        if (theDB == null) {
            Toast.makeText(this, "Try again in a few seconds.", Toast.LENGTH_SHORT).show();
        }
        else {
            String selection = "_id = " + currentRow;
            Toast.makeText(this, "You have deleted user with ID "+selection, Toast.LENGTH_SHORT).show();

            theDB.delete("user", selection, null);
            this.clearFields();
            this.changeEditFieldVisibility(View.GONE);
            this.updateTable();
        }
    }


    public void updateTable(){
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

    public void btnRefreshClick(View view) {
       this.updateTable();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (theDB != null) theDB.close();
    }

}

