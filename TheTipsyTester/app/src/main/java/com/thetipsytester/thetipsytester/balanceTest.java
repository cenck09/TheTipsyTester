package com.thetipsytester.thetipsytester;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nick on 4/11/2016.
 */
public class balanceTest extends Activity implements SensorEventListener{


    Sensor accelerometer;
    SensorManager sm;

    //TextView acceleration;
    TextView timerText;

    boolean calibration = false;
    double bac = 0;

    int finalScore;
    boolean measuring = false, waiting = false;
    float totalAcceleration = 0, changeAcceleration = 0, lastTotal = 0, x, y ,z;
    final long startTime = 10000, pauseTime = 3000, interval=1000;

    CountDownTimer countDownTimer = new MyCountDownTimer(startTime, interval);
    CountDownTimer pauseTimer = new MyCountDownTimer2(pauseTime, interval);

    ArrayList<String> nextTests;

    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_test);

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer= sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        //acceleration = (TextView)findViewById(R.id.acceleration);
        timerText = (TextView)findViewById(R.id.balanceCountDown);
        timerText.setText(timerText.getText()+String.valueOf(startTime / 1000));

        Intent intent = getIntent();
        nextTests = intent.getStringArrayListExtra("nextTests");
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);

        System.out.println("Calibraion: " + calibration);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Balance Test");
        alertDialogBuilder.setMessage("To perform this test, stand on one leg and hold your arms out to your sides, phone in hand.\n\nHold this position for 10 seconds while the phone measures your balance.\n\nYou will have 3 seconds after clicking 'Start' to get into position.");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        waiting = true;
                        measure();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        View view = this.getWindow().getDecorView();

        String color = sharedPref.getString("color", "232323");

        view.setBackgroundColor(Color.parseColor("#" + color));
    }

    public class MyCountDownTimer extends CountDownTimer{
        public MyCountDownTimer(long startTime, long interval){
            super(startTime, interval);
        }

        @Override
        public void onFinish(){
            measuring = false;
            lastTotal = totalAcceleration;
            totalAcceleration = 0;
            finalScore = (int)lastTotal;

            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

            Intent intent = new Intent(balanceTest.this, scorereportActivity.class);
            intent.putExtra("prevTest", "balance");
            intent.putStringArrayListExtra("nextTests", nextTests);
            intent.putExtra("score", finalScore);
            intent.putExtra("calibration", calibration);
            intent.putExtra("BAC", bac);
            timerText.setText("Score: " + finalScore);
            startActivity(intent);
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished){
            timerText.setText("Measuring: " + Math.round(millisUntilFinished / (long)1001));

        }
    }

    public class MyCountDownTimer2 extends CountDownTimer{
        public MyCountDownTimer2(long startTime, long interval){
            super(startTime, interval);
        }
        @Override
        public void onFinish(){
            countDownTimer.start();
            measuring = true;
            waiting = false;
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
        }
        @Override
        public void onTick(long millisUntilFinished){
            timerText.setText("Starts in: " + Math.round(millisUntilFinished / (long) 1001));
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        changeAcceleration = Math.abs(Math.abs(x) + Math.abs(y) + Math.abs(z) - (float) 10.2);
        if (measuring) {
            totalAcceleration = totalAcceleration + changeAcceleration;
        }
        //acceleration.setText("measurements: " + totalAcceleration +
        //                    "\ntotal: " + lastTotal);
    }

    public void measure(){
        pauseTimer.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
