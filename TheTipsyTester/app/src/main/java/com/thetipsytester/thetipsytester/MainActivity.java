package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
