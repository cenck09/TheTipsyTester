package com.thetipsytester.thetipsytester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.logging.Handler;

public class comet_smash extends AppCompatActivity {

    static final String STATE_GAME_TIME = "CometSmashGameTime";
    static final String STATE_GAME_INITENT_ROCK_THROW = "CometSmashGameBrekRock";
    int time;
    cometView comet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_smash);
        this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#" + PreferenceManager.getDefaultSharedPreferences(this).getString("color", "232323")));
        logError("Create teh Comet smash game!!");


        LocalBroadcastManager.getInstance(this).registerReceiver(breakRockReceiver,new IntentFilter(STATE_GAME_INITENT_ROCK_THROW));

    }


    private BroadcastReceiver breakRockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logError("Got message: " + intent.getStringExtra("message"));
        }
    };



    public void endGame(){
        logError("END GAMEEEEE!!!!");

        Intent currentIntent = getIntent();
        Intent intent = new Intent(this, scorereportActivity.class);

        intent.putExtra("prevTest", "comet"); // this needs to be updated
        intent.putStringArrayListExtra("nextTests",currentIntent.getStringArrayListExtra("nextTests"));
        intent.putExtra("score", time);
        intent.putExtra("calibration", currentIntent.getBooleanExtra("calibration", false));
        intent.putExtra("BAC", currentIntent.getDoubleExtra("BAC", 0));

        startActivity(intent);
    }
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            if(comet == null){
                comet = new cometView(this);
                comet.setVisibility(View.INVISIBLE);

                ((RelativeLayout)findViewById(R.id.space)).addView(comet);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            comet.setVisibility(View.VISIBLE);
                        } catch (Exception ex) {
                        }
                    }
                }, 1000);

            }
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        logError("PAUSED COMET SMASH");
    }
    @Override
    public void onResume(){
        super.onResume();
        logError("RESUMED COMET SMASH");
    }
    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(breakRockReceiver);

        super.onDestroy();
        logError("DESTROYED COMET SMASH");
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        logError("SAVED INSTANCE STATE FOR COMET_SMASH!");
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logError("RESTORED INSTANCE STATE FOR COMET_SMASH!");
    }

    public void logError(String err){
        Log.d("COMET_SMASH ", err);
    }
}
