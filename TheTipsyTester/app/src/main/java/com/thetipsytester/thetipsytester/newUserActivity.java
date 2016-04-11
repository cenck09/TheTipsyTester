package com.thetipsytester.thetipsytester;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class newUserActivity extends AppCompatActivity {

    Long rowid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        if (getIntent().hasExtra("rowid")) {
            rowid = getIntent().getLongExtra("rowid", 0);
            ContentResolver cr = getContentResolver();

            Cursor c = cr.query(UserContentProvider.CONTENT_URI.buildUpon().appendPath(Long.toString(rowid)).build(),
                    new String[] {"name","gender","weight"},null, null, null);

            if (!c.moveToFirst()) {
                this.setTitle("Add new user");
                Toast.makeText(this, "Error retrieving user.  Adding new user, instead.", Toast.LENGTH_LONG).show();
                rowid = null;
            }
            else {
                this.setTitle("Edit user");
                ((EditText) findViewById(R.id.NameEditText)).setText(c.getString(0));

                RadioButton male = (RadioButton) findViewById(R.id.maleRadioButton);
                RadioButton female = (RadioButton) findViewById(R.id.femaleRadioButton);
                if(c.getString(1).equals("male")){
                    male.setChecked(true);
                }else{
                    female.setChecked(true);
                }


                ((EditText) findViewById(R.id.WeightEditText)).setText(c.getString(2));
            }
            c.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public void btnAddClick(View view) {
        ContentResolver cr = getContentResolver();

        RadioButton male = (RadioButton)findViewById(R.id.maleRadioButton);

        ContentValues values = new ContentValues();
        values.put("name", ((EditText) findViewById(R.id.NameEditText)).getText().toString());
        if(male.isChecked()){
            values.put("gender", "male");
            //System.out.println("MALE");
        }else{
            values.put("gender", "female");
            //System.out.println("FEMALE");
        }
        values.put("weight", ((EditText) findViewById(R.id.WeightEditText)).getText().toString());

        try {
            if (rowid == null) {
                cr.insert(UserContentProvider.CONTENT_URI, values);
            } else {
                cr.update(UserContentProvider.CONTENT_URI.buildUpon().appendPath(Long.toString(rowid)).build(), values, null, null);
            }
            finish();
        } catch (SQLException e) {
            Toast.makeText(this, "Error updating database.", Toast.LENGTH_LONG).show();
        }
    }
}
