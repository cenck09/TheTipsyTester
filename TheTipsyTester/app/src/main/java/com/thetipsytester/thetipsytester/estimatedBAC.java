package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class estimatedBAC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimated_bac);

        Intent intent = getIntent();
        int bacCount = intent.getIntExtra("bacCount", 0);
        int numTests = intent.getIntExtra("numTests", 0);
        int estimate = bacCount/numTests;
        String finalEstimate;

        System.out.println("bacCount: " + bacCount + "      numTests: " + numTests);
        System.out.println("estimate: " + estimate);

        if(estimate == 1){
            finalEstimate = "0.00-0.04";
        }else if(estimate == 2){
            finalEstimate = "0.04-0.08";
        }else if(estimate == 3){
            finalEstimate = "0.08-0.12";
        }else if(estimate == 4){
            finalEstimate = "0.12-0.16";
        }else if(estimate == 5){
            finalEstimate = "0.16-0.20";
        }else if(estimate == 6){
            finalEstimate = ">.20";
        }else{
            finalEstimate = "0.00";
        }


        TextView eBAC = (TextView) findViewById(R.id.finalBACtag);

        eBAC.setText("Estimated BAC: " + finalEstimate);

    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public void goHome(View view){
        finish();
    }
}
