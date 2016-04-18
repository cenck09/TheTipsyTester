package com.thetipsytester.thetipsytester;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class bacCalculatorActivity extends AppCompatActivity {

    Long rowid;
    String name = "John",gender = "male";
    int bodyWeight = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bac_calculator);



        if (getIntent().hasExtra("rowid")) {
            TipsyDB tipsy = new TipsyDB(this);
            SQLiteDatabase db = tipsy.getWritableDatabase();

            rowid = getIntent().getLongExtra("rowid", 0);

            String selectQuery = "SELECT * FROM " + "users" + " WHERE _id = " + rowid;

            Cursor c = db.rawQuery(selectQuery, null);

            if(c != null){
                c.moveToFirst();
            }

            User u = new User();
            u.setId(c.getInt(c.getColumnIndex("_id")));
            u.setName(c.getString(c.getColumnIndex("name")));
            u.setGender(c.getString(c.getColumnIndex("gender")));
            u.setWeight(c.getInt(c.getColumnIndex("weight")));

            name = u.getName();
            gender = u.getGender();
            bodyWeight = u.getWeight();

            TextView userText = (TextView)findViewById(R.id.userText);
            userText.setText("Current User: " + name);
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


    public void getUser(View view){
        Intent intent = new Intent(this, userSelectActivity.class);
        intent.putExtra("activity", "BAC");

        startActivity(intent);
    }

    public void calculateBAC(View view){


        double bProof = Double.parseDouble(((EditText)findViewById(R.id.beerProof)).getText().toString());
        double bNum = Double.parseDouble(((EditText)findViewById(R.id.beerNum)).getText().toString());

        double wProof = Double.parseDouble(((EditText)findViewById(R.id.wineProof)).getText().toString());
        double wNum = Double.parseDouble(((EditText)findViewById(R.id.wineNum)).getText().toString());

        double sProof = Double.parseDouble(((EditText)findViewById(R.id.shotsProof)).getText().toString());
        double sNum = Double.parseDouble(((EditText)findViewById(R.id.shotsNum)).getText().toString());

        double hours = Double.parseDouble((((EditText)findViewById(R.id.hourNum)).getText().toString()));


        double gramsOfBeer = 0.789*29.5735*(bProof/200)*bNum*12;
        double gramsOfWine = 0.789*29.5735*(wProof/200)*wNum*5;
        double gramsOfShots = 0.789*29.5735*(sProof/200)*sNum*1.5;
        double gramsOfAlcohol = gramsOfBeer+gramsOfWine+gramsOfShots;
        double bac = 0;


        if(gender.equals("male")){
            bac = ((gramsOfAlcohol)/(453.592*bodyWeight * 0.68))*100 - 0.015*hours;
        }else if(gender.equals("female")){
            bac = (((gramsOfAlcohol)/(453.592*bodyWeight * 0.55))* 100)- 0.015*hours;
        }


        TextView calculatedVal = (TextView) findViewById(R.id.calculatedBAC);
        if(bac<0){
            bac = 0;
            calculatedVal.setText("0.000%");
        }else {

            String retVal = Double.toString(bac);
            retVal = retVal.substring(0, 5);
            calculatedVal.setText(retVal + "%");
        }




    }

}
