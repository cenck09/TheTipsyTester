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
import android.widget.Button;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Amanda on 4/20/2016.
 */
public class patternTest extends AppCompatActivity{

    private int numBlocks=0;
    private Button button = null;
    private int nanoSec;
    int[] randPattern = new int[10];
    int y = 0;
    int runCount = 0;
    int score = 0;

    String[] nextTests;
    boolean calibration = false;
    double bac = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pattern_test);

        Intent intent = getIntent();
        nextTests = intent.getStringArrayExtra("nextTests");
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Memory Test");
        alertDialogBuilder.setMessage("To perform this test, pay attention to the pattern that blinks on the blocks. \n\nWhen the pattern is done you must select the blocks in the correct order. \n\nThe test will begin when you click 'Start'.");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startGame();
                    }
                });

        alertDialogBuilder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public void startGame() {
        if(runCount < 4) {
            int temp;
            final Random rand = new Random();
            final Timer timer = new Timer();

            int x = 0;
            y=0;
            randPattern[0]=0;
            randPattern[1]=0;
            randPattern[2]=0;
            randPattern[3]=0;
            randPattern[4]=0;
            randPattern[5]=0;
            randPattern[6]=0;
            randPattern[7]=0;
            randPattern[8]=0;
            randPattern[9]=0;

            nanoSec = 1000;
            if (runCount == 0) {
                numBlocks = 2;
            }
            if (runCount == 1 || runCount == 2) {
                numBlocks = 4;
            }
            if (runCount == 3) {
                numBlocks = 6;
            }
            for(int i = 0; i<numBlocks; ++i) {
                temp = rand.nextInt(9) + 1;

                switch (temp) {
                    case 1: {
                        button = (Button) findViewById(R.id.button1);
                        randPattern[x + 1] = 1;
                        x++;
                    }break;

                    case 2: {
                        button = (Button) findViewById(R.id.button2);
                        randPattern[x+1]=2;
                        x++;
                    }break;

                    case 3: {
                        button = (Button) findViewById(R.id.button3);
                        randPattern[x+1]=3;
                        x++;
                    }break;

                    case 4: {
                        button = (Button) findViewById(R.id.button4);
                        randPattern[x+1]=4;
                        x++;
                    }break;

                    case 5: {
                        button = (Button) findViewById(R.id.button5);
                        randPattern[x+1]=5;
                        x++;
                    }break;

                    case 6: {
                        button = (Button) findViewById(R.id.button6);
                        randPattern[x+1]=6;
                        x++;
                    }break;

                    case 7: {
                        button = (Button) findViewById(R.id.button7);
                        randPattern[x+1]=7;
                        x++;
                    }break;

                    case 8: {
                        button = (Button) findViewById(R.id.button8);
                        randPattern[x+1]=8;
                        x++;
                    }break;

                    case 9: {
                        button = (Button) findViewById(R.id.button9);
                        randPattern[x+1]=9;
                        x++;
                    }break;
                }

                timer.schedule(new MyTask(button, temp), nanoSec);
                timer.schedule(new MyTask(button, -1), nanoSec + 500);
                nanoSec += 1000;
            }
            runCount++;
        }
        else {
            //score = (int) Math.round((score / 160) * 100);
            Intent intent = new Intent(this, scorereportActivity.class);
            intent.putExtra("prevTest", "pattern");
            intent.putExtra("nextTests", nextTests);
            intent.putExtra("score", score);
            intent.putExtra("calibration", calibration);
            intent.putExtra("BAC", bac);
            startActivity(intent);
        }
    }


    class MyTask extends TimerTask {
        private Button buttonId;
        private int buttonNum;

        public MyTask(Button buttonId, int temp) {
            super();
            this.buttonId = buttonId;
            buttonNum = temp;
        }

        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(buttonNum == -1){
                        buttonId.setBackgroundColor(Color.BLACK);
                    }
                    else {
                        buttonId.setBackgroundColor(Color.YELLOW);
                    }
                }
            });
        }
    }

    public void userGuess(View view) {
        final Timer timer = new Timer();
        nanoSec = 400;
        switch(view.getId()) {
            case R.id.button1: {
                button = (Button) findViewById(R.id.button1);

                if (randPattern[y+1]==1) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button2: {
                button = (Button) findViewById(R.id.button2);

                if (randPattern[y+1]==2) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button3: {
                button = (Button) findViewById(R.id.button3);

                if (randPattern[y+1]==3) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button4: {
                button = (Button) findViewById(R.id.button4);

                if (randPattern[y+1]==4) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button5: {
                button = (Button) findViewById(R.id.button5);

                if (randPattern[y+1]==5) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button6: {
                button = (Button) findViewById(R.id.button6);

                if (randPattern[y+1]==6) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button7: {
                button = (Button) findViewById(R.id.button7);

                if (randPattern[y+1]==7) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button8: {
                    button = (Button) findViewById(R.id.button8);

                if (randPattern[y+1]==8) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
            case R.id.button9: {
                    button = (Button) findViewById(R.id.button9);

                if (randPattern[y+1]==9) {
                    view.setBackgroundColor(Color.GREEN);
                    y++;
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    score = score + 10;
                    if(randPattern[y+1] == 0){
                        startGame();
                    }
                } else {
                    view.setBackgroundColor(Color.RED);
                    timer.schedule(new MyTask(button, -1), nanoSec);
                    startGame();
                }
            }break;
        }
    }
}
