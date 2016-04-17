package com.thetipsytester.thetipsytester;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

        checkFirstRun();

    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }


    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Terms and Conditions");
            alertDialogBuilder.setMessage("This application should not be used as an accurate measure for intoxication level." +
                    "\n\nThe makers of this app do not condone underage drinking or drinking and driving." +
                    "\n\nIn order to use this app, you must agree to these terms.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, update first run
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isFirstRun", false)
                                    .apply();

                        }
                    })
                    .setNegativeButton("I don't agree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close the activity
                            MainActivity.this.finish();

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        }
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
