package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        setContentView(R.layout.activity_main);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");
        System.out.println("HERE'S THE COLOR: " + color);

        view.setBackgroundColor(Color.parseColor("#" + color));
    }


    public void testPage(View view){
        //When the Test button is clicked from the main menu
        Intent intent = new Intent(this, testSelectActivity.class);
        //Intent intent = new Intent(this, userSelectActivity.class);

        startActivity(intent);
    }

    public void baselinePage(View view){
        //When the Baseline button is clicked from the main menu
        Intent intent = new Intent(this, baselineActivity.class);

        startActivity(intent);
    }

    public void bacCalculator(View view){
        //When the BAC Calculator button is clicked from the main menu
        Intent intent = new Intent(this, bacCalculatorActivity.class);

        startActivity(intent);
    }

    public void scores(View view){
        //When the Scores button is clicked from the main menu
        Intent intent = new Intent(this, scoresActivity.class);

        startActivity(intent);
    }

    public void userSelect(View view) {
        Intent intent = new Intent(this, userSelectActivity.class);

        startActivity(intent);
    }

    public void settings(View view){
        //When the Settings button is clicked from the main menu
        Intent intent = new Intent(this, settingsActivity.class);

        startActivity(intent);
    }

}
