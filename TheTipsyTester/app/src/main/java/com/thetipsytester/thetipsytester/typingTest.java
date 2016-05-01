package com.thetipsytester.thetipsytester;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Amanda on 4/22/2016.
 */
public class typingTest extends AppCompatActivity {
    String sentence1 = "The production of alcohol started 12,000 years ago.";
    String sentence2 = "Vodka means 'little water' in Russian.";
    String sentence3 = "The world's oldest known recipe is for beer.";
    String sentence4 = "A bottle of champagne contains 49 million bubbles.";
    String sentence5 = "One shot of vodka contains as much alcohol as an entire beer.";

    String selection;
    Random r = new Random();

    long score = 0;
    int finalScore = 0;
    int bacCount, numTests;
    long startTime;
    long countUp;
    Chronometer stopWatch;

    ArrayList<String> nextTests;
    boolean calibration = false;
    double bac = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.typing_test);

        Intent intent = getIntent();
        nextTests = intent.getStringArrayListExtra("nextTests");
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);
        bacCount = intent.getIntExtra("bacCount", 0);
        numTests = intent.getIntExtra("numTests", 0);



        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Typing Test");
        alertDialogBuilder.setMessage("For this test, you will be given a random sentence. \n\nYou must retype the sentence and click 'Done' when you are finished typing. \n\nThe test will begin when you click 'Start'.");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       startGame();
                    }
                });
        alertDialogBuilder.show();
    }

    public void startGame() {
        stopWatch = (Chronometer) findViewById(R.id.time);
        startTime = SystemClock.elapsedRealtime();
        stopWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                countUp = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
            }
        });
        stopWatch.setBase(SystemClock.elapsedRealtime());
        stopWatch.start();

        int randNum = r.nextInt(2) + 1;
        if (randNum == 1) {
            selection = sentence1;
        }if (randNum == 2) {
            selection = sentence2;
        }if (randNum == 3) {
            selection = sentence3;
        }if (randNum == 4) {
            selection = sentence4;
        }if (randNum == 5) {
            selection = sentence5;
        }

        TextView sentence = (TextView) findViewById(R.id.sentence);
        sentence.setText(selection);
        sentence.setTextColor(Color.BLACK);
    }

    public void sentenceComparison(View view) {
        stopWatch.stop();
        EditText editText = (EditText) findViewById(R.id.userSentence);
        String userSentence = editText.getText().toString();

        for (int i = 0; i < userSentence.length(); i++) {
            if(selection.length()> i) {
                if (userSentence.charAt(i) == selection.charAt(i)) {
                    score = score + 5;
                }
            }
        }
        long temp;

        score = ((score*100) / (selection.length() * 5));
        //takes off 2 points for every 15 seconds more than 15
        if (countUp > 15) {
            countUp = countUp - 15;
            temp = countUp / 15;
            temp = temp * 2;
            score = score - temp;
        }

        finalScore = (int) score;
        if (finalScore < 0){
            finalScore = 0;
        }


        Intent intent = new Intent(this, scorereportActivity.class);
        intent.putExtra("prevTest", "typing");
        intent.putStringArrayListExtra("nextTests", nextTests);
        intent.putExtra("score", finalScore);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        intent.putExtra("bacCount", bacCount);
        intent.putExtra("numTests", numTests);
        System.out.println("bacCOUNT: " + bacCount + "\n\n\n");
        startActivity(intent);

    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();
        String color = sharedPref.getString("color", "232323");
        view.setBackgroundColor(Color.parseColor("#" + color));
    }

}
