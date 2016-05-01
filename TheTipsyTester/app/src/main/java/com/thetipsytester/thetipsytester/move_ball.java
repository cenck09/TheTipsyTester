package com.thetipsytester.thetipsytester;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import com.thetipsytester.thetipsytester.moveBall.Game;
import com.thetipsytester.thetipsytester.moveBall.DrawingPanel;

/**
 * Created by Nigel
 */
public class move_ball extends AppCompatActivity{

    int score = 0;
    int timeInSec = 0;
    int bacCount, numTests;
    private DrawingPanel drawingPanel;
    private TextView timerText;
    private Game game;
    ArrayList<String> nextTests;
    boolean calibration = false;
    double bac = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_ball);

        drawingPanel = (DrawingPanel) findViewById(R.id.drawingPanel);
        timerText = (TextView) findViewById(R.id.timerText);

        drawingPanel.post(new Runnable() {
            @Override
            public void run() {
                createGame(drawingPanel.getMeasuredWidth(), drawingPanel.getMeasuredHeight());
            }
        });
        Intent intent = getIntent();
        nextTests = intent.getStringArrayListExtra("nextTests");
        calibration = intent.getBooleanExtra("calibration", false);
        bac = intent.getDoubleExtra("BAC", 0);
        bacCount = intent.getIntExtra("bacCount", 0);
        numTests = intent.getIntExtra("numTests", 0);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Move Ball");
        alertDialogBuilder.setMessage("To perform this test, pay attention to positions of black, blue holes and red ball. \n\n" +
                "Each time, you move red ball to the specific blue hole, then it shows next hole you move to. \n\n" +
                "Time limit is 3 minutes. If you are ready, please click 'Start'");
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        game.startGame();
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

    private void createGame(int x, int y) {
        game = new Game(x, y, this);
    }


    public void updateTimerText(int time){
        timeInSec = time;
        int seconds = time % 60;
        int minutes = time / 60;
        final String timeString = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   timerText.setText(timeString);
                               }
                           }
        );
    }

    public void updateDrawingPanel(final Game game) {
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   drawingPanel.update(game);
                               }}
        );

    }

    public void showResult(boolean t) {
        if(t)
            score = (game.TIME_LIMIT - timeInSec)*2;
        else
            score = 0;
        score = score < 0 ? 0:score;
        Intent intent = new Intent(this, scorereportActivity.class);
        intent.putExtra("prevTest", "move_ball");
        intent.putStringArrayListExtra("nextTests", nextTests);
        intent.putExtra("score", score);
        intent.putExtra("calibration", calibration);
        intent.putExtra("BAC", bac);
        intent.putExtra("bacCount", bacCount);
        intent.putExtra("numTests", numTests);
        System.out.println("bacCOUNT: " + bacCount + "\n\n\n");
        startActivity(intent);
    }

}
