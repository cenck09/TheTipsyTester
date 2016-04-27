package com.thetipsytester.thetipsytester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class singleTestSelectActivity extends AppCompatActivity{

    boolean calibration = false;
    double bac;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singletest_select);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);
        System.out.println("Calibraion: " + calibration);

    }

    @Override
    protected void onResume() {
        super.onResume();

        View view = this.getWindow().getDecorView();
        String color = sharedPref.getString("color", "232323");
        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public void startComet(View view){
        Intent intent = new Intent(this, comet_smash.class);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        startActivity(intent);
    }

    public void startSchwacking(View view){
        Intent intent = new Intent(this, schwack_a_moleaa.class);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        startActivity(intent);
    }

    public void startBalance(View view){
        Intent intent = new Intent(this, balanceTest.class);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        startActivity(intent);
    }

    public void startPattern (View view){
        Intent intent = new Intent(this, patternTest.class);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        startActivity(intent);
    }

    public void startTyping(View view) {
        Intent intent = new Intent(this, typingTest.class);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        startActivity(intent);
    }

}
