package com.thetipsytester.thetipsytester;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class bacCalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bac_calculator);
    }

    public void calculateBAC(View view){


        double bProof = Double.parseDouble(((EditText)findViewById(R.id.beerProof)).getText().toString());
        double bNum = Double.parseDouble(((EditText)findViewById(R.id.beerNum)).getText().toString());

        double wProof = Double.parseDouble(((EditText)findViewById(R.id.wineProof)).getText().toString());
        double wNum = Double.parseDouble(((EditText)findViewById(R.id.wineNum)).getText().toString());

        double sProof = Double.parseDouble(((EditText)findViewById(R.id.shotsProof)).getText().toString());
        double sNum = Double.parseDouble(((EditText)findViewById(R.id.shotsNum)).getText().toString());

        double hours = Double.parseDouble((((EditText)findViewById(R.id.hourNum)).getText().toString()));

        int bodyWeight = 170;

        double gramsOfBeer = 0.789*29.5735*(bProof/200)*bNum*12;
        double gramsOfWine = 0.789*29.5735*(wProof/200)*wNum*5;
        double gramsOfShots = 0.789*29.5735*(sProof/200)*sNum*1.5;
        double gramsOfAlcohol = gramsOfBeer+gramsOfWine+gramsOfShots;
        double bac = 0;

        String gender = "male";

        if(gender.equals("male")){
            bac = ((gramsOfAlcohol)/(453.592*bodyWeight * 0.68))*100 - 0.015*hours;
        }else if(gender.equals("female")){
            bac = (((gramsOfAlcohol)/(453.592*bodyWeight * 0.55))* 100)- 0.015*hours;
        }

        String retVal = Double.toString(bac);
        retVal = retVal.substring(0,5);

        TextView calculatedVal = (TextView) findViewById(R.id.calculatedBAC);
        calculatedVal.setText(retVal + "%");

    }

}
