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
        EditText beerP = (EditText)findViewById(R.id.beerProof);
        EditText wineP = (EditText)findViewById(R.id.wineProof);
        EditText shotsP = (EditText)findViewById(R.id.shotsProof);
        EditText beerQ = (EditText)findViewById(R.id.beerNum);
        EditText wineQ = (EditText)findViewById(R.id.wineNum);
        EditText shotsQ = (EditText)findViewById(R.id.shotsNum);
        TextView bacDisplay = (TextView)findViewById(R.id.bacPercent);

        int bp = Integer.parseInt(beerP.getText().toString());
        int wp = Integer.parseInt(wineP.getText().toString());
        int sp = Integer.parseInt(shotsP.getText().toString());
        int bq = Integer.parseInt(beerQ.getText().toString());
        int wq = Integer.parseInt(wineQ.getText().toString());
        int sq = Integer.parseInt(shotsQ.getText().toString());

        double bGrams = (12*bq)*29.5735*(bp/2)*.789;
        double wGrams = (8*wq)*29.5735*(wp/2)*.789;
        double sGrams = (1.5*sq)*29.5735*(sp/2)*.789;

        double userPounds = 170;
        String userGender = "male";

        double grams = bGrams + wGrams + sGrams;
        double bodyGrams = userPounds*453.592;
        double bac = 0;

        if(userGender.equals("male")){
            bac = (grams/(bodyGrams * .68));
        }else{
            bac = (grams/(bodyGrams * .55));
        }


        String bacRetVal = bac+"";
        bacRetVal=bacRetVal.substring(0,5) + "%";

        bacDisplay.setText(bacRetVal);
    }



}
