package com.thetipsytester.thetipsytester;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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
    TipsyDB tipsy;
    SQLiteDatabase db;
    boolean calibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        tipsy = new TipsyDB(this);
        db = tipsy.getWritableDatabase();

        if (getIntent().hasExtra("rowid")) {


            rowid = getIntent().getLongExtra("rowid", 0);

            String selectQuery = "SELECT * FROM " + "users" + " WHERE id = " + rowid;

            Cursor c = db.rawQuery(selectQuery, null);


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
        String name, gender, weight;

        RadioButton male = (RadioButton)findViewById(R.id.maleRadioButton);

        ContentValues values = new ContentValues();

        name = ((EditText) findViewById(R.id.NameEditText)).getText().toString();
        values.put("name", name);

        if(male.isChecked()){
            gender = "male";
            values.put("gender", gender);
        }else{
            gender = "female";
            values.put("gender", gender);
        }
        weight = ((EditText) findViewById(R.id.WeightEditText)).getText().toString();
        values.put("weight", weight);

        try {
            if (rowid == null) {
                db.insert("users", null, values);
            } else {
                //db.update();
            }

            finish();
        } catch (SQLException e) {
            Toast.makeText(this, "Error updating database.", Toast.LENGTH_LONG).show();
        }
    }
}
