package com.thetipsytester.thetipsytester;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;


public class comet_smash extends AppCompatActivity {

    private static final SecureRandom random = new SecureRandom();

    static final String STATE_GAME_TIME = "CometSmashGameTime";
    static final String STATE_GAME_SCORE = "CometSmashGameScore";

     // this makes the comet easier to tap
    // it adds half the value to each side
    int TOUCH_TOLERANCE = 50;

    // for speeds, the higher the number the faster the object
    int comet_speed = 700;
    int comet_size = 80;
    int comet_triangle_count = 20;

    int debris_count_min = 3;
    int debris_count_max = 12;

    int debris_triangle_count_min = 2;
    int debris_triangle_count_max = 5;

    int debris_size_min = 20;
    int debris_size_max = 35;

    int debris_speed_max = 1000; // debris can be slow or fast
    int debris_speed_min = 50;

    private static Timer timer = new Timer();
    TimerTask timerTask;
    int count;
    boolean shouldCount;
    boolean firstRun;
    AlertDialog testA;

    cometView comet;
    Integer hitCount = 10;

    public CountDownTimer gameCounter;

    private int scaleValue(int value){
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                logError("TouchDown found at X="+x+" Y+"+y);
                logError("Comet found at X=" + comet.animatorTarget.currentLoc.x + " Y+" + comet.animatorTarget.currentLoc.y);
                if(((y<(comet.animatorTarget.currentLoc.y+(comet.COMET_SIZE+(TOUCH_TOLERANCE/2))))&&(y>comet.animatorTarget.currentLoc.y-(comet.COMET_SIZE+(TOUCH_TOLERANCE/2))))&&((x<(comet.animatorTarget.currentLoc.x+(comet.COMET_SIZE+(TOUCH_TOLERANCE/2))))&&(x>comet.animatorTarget.currentLoc.x-(comet.COMET_SIZE+(TOUCH_TOLERANCE/2))))){
                    logError("HIT COMET!!");
                    cometHit(new Tuple<Integer, Integer>(x,y));
                }
            break;
        }
        return false;
    }

    private void cometHit(final Tuple<Integer,Integer> loc){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((TextView)findViewById(R.id.hitCount)).setText(hitCount+"");
                    comet.removeChunks();
                    hitCount--;
                    logError("HIT COUNT = "+hitCount);
                    spawnDebris(loc);
                    if (hitCount <= 0) endGame();

                } catch (Exception ex) {
                    logError("Oh snap no spawning or ending! exception is  || "+ex.toString());
                }
            }
        });
    }

    private void spawnDebris(Tuple<Integer,Integer> loc){
        int ni = (random.nextInt(debris_count_max-debris_count_min)+debris_count_min);
        logError("calling spawn Debris with count = "+ni);
        for(int i = 0; i<ni;i++){
            int debrisTriangleCount = random.nextInt(debris_triangle_count_max-debris_count_min)+debris_triangle_count_min;
            int debrisSize = random.nextInt(debris_size_max-debris_size_min)+debris_size_min;
            int debrisSpeed = random.nextInt(debris_speed_max-debris_speed_min)+debris_speed_min;

            logError("Spawning Debris with size = "+debrisSize+" | triangle count = "+debrisTriangleCount+" | speed = "+debrisSpeed+" at X="+loc.x+" Y="+loc.y);

            cometView debris = new cometView(this,debrisTriangleCount , debrisSize ,debrisSpeed, true, true);

            RelativeLayout space =   ((RelativeLayout)findViewById(R.id.space));

            debris.X_MAX = space.getWidth()-debris.X_MIN;
            debris.Y_MAX = space.getHeight()-debris.Y_MIN;
            debris.forceRotation();
            space.addView(debris);
            debris.lParams.topMargin = loc.y;
            debris.lParams.leftMargin = loc.x;

            debris.initRandomTrajectory(loc.x,loc.y);

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_smash);
        this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#" + PreferenceManager.getDefaultSharedPreferences(this).getString("color", "232323")));
        logError("Create teh Comet smash game!!");
        TOUCH_TOLERANCE = scaleValue(TOUCH_TOLERANCE);
        comet_size = scaleValue(comet_size);

        debris_size_max = scaleValue(debris_size_max);
        debris_size_min = scaleValue(debris_size_min);

        if(!isTablet(this)){
            logError("shrinking for phones");
            double factor = 0.75;
            comet_size = (int)(comet_size*(factor));
            TOUCH_TOLERANCE = (int)(TOUCH_TOLERANCE*(factor));
            debris_size_max = (int)(debris_size_max*(factor));
            debris_size_min = (int)(debris_size_min*(factor));
        }

        if(savedInstanceState != null){
            count = savedInstanceState.getInt(STATE_GAME_TIME);
            hitCount = savedInstanceState.getInt(STATE_GAME_SCORE);
            firstRun = savedInstanceState.getBoolean("firstRun");
        }else{
            firstRun = true;
            count = 0;
        }
    }

    private void setGameClockBoard(){
        ((TextView)findViewById(R.id.timeView)).setText("" + ((double) count));
    }


    private BroadcastReceiver breakRockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logError("Got Break Rock Message");
        }
    };
    private BroadcastReceiver endGameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logError("Got end game Message");
            endGame();
        }
    };

    private void startGameCount(long time){
        logError("starting Game timer");

        shouldCount = true;
        if(timerTask != null) timerTask.cancel();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clockIt();
                    }
                });
            }
        };
    timer.scheduleAtFixedRate(timerTask,0,1000);

    }

    private void clockIt(){
        if(shouldCount) count++;
        setGameClockBoard();
    }

    private void startGame(){
        startGameCount(count);
        launchComet();

    }

    private void endGame(){
        logError("END GAMEEEEE!!!!");

        Intent currentIntent = getIntent();
        Intent intent = new Intent(this, scorereportActivity.class);

        intent.putExtra("prevTest", "comet"); // this needs to be updated
        intent.putExtra("nextTests", currentIntent.getStringArrayListExtra("nextTests"));
        intent.putExtra("score", count);
        intent.putExtra("calibration", currentIntent.getBooleanExtra("calibration", false));
        intent.putExtra("BAC", currentIntent.getDoubleExtra("BAC", 0));
        intent.putExtra("bacCount", currentIntent.getIntExtra("bacCount", 0));
        intent.putExtra("numTests", currentIntent.getIntExtra("numTests", 0));

        startActivity(intent);
        finish();
    }

    private void launchComet(){
        ((RelativeLayout) findViewById(R.id.space)).setClickable(false);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus){
            if(comet == null){
                comet = new cometView(this, comet_triangle_count, comet_size,comet_speed, false,false);

                ((RelativeLayout) findViewById(R.id.space)).addView(comet);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final RelativeLayout space = ((RelativeLayout) findViewById(R.id.space));//.addView(comet);
                            comet.initRandomTrajectory( -comet.COMET_SIZE, -comet.COMET_SIZE);
                        } catch (Exception ex) {
                        }
                    }
                }, 100);

            }
        }

    }

    private void testAlert(){
        AlertDialog.Builder test = new AlertDialog.Builder(this);
        test.setTitle("Comet smash Test").setMessage("hit the comet until it breaks! \n\nGood Luck!!");
        test.setCancelable(false).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                performReadyGameCountDown();
                firstRun = false;
            }
        });
        testA = test.create();
        testA.show();
    }



    @Override
    public void onPause() {
        super.onPause();

        shouldCount = false;
        //if(timer != null) timer.cancel();
        if(timerTask != null) timerTask.cancel();
        logError("Purged time for count = " + timer.purge());
        if(comet != null){
            comet.killComet();
            comet = null;
        }

        if(testA != null) testA.cancel();

        logError("PAUSED COMET SMASH");
    }
    @Override
    public void onResume() {
        super.onResume();
        logError("RESUMED COMET SMASH");
        if(firstRun) testAlert();
        else performReadyGameCountDown();
        ((TextView) findViewById(R.id.hitCount)).setText(hitCount + "");
    }
    @Override
    public void onDestroy(){
        if(testA != null) testA.cancel(); testA = null;
        super.onDestroy();
        logError("DESTROYED COMET SMASH");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        logError("SAVED INSTANCE STATE FOR COMET_SMASH!");
        savedInstanceState.putInt(STATE_GAME_TIME, count);
        savedInstanceState.putInt(STATE_GAME_SCORE, hitCount);
        savedInstanceState.putBoolean("firstRun", firstRun);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logError("RESTORED INSTANCE STATE FOR COMET_SMASH!");
        count = savedInstanceState.getInt(STATE_GAME_TIME);
        hitCount = savedInstanceState.getInt(STATE_GAME_SCORE);
        firstRun = savedInstanceState.getBoolean("firstRun");
    }
    private void addCountDownViewForValue(final int value){
        RelativeLayout arena = (RelativeLayout)findViewById(R.id.space);
        final TextView counterView = new TextView(this);
        counterView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        counterView.setTextSize(50);
        int size = 200;
        switch (value){
            case 1:
                counterView.setText("GO!!!");
                break;
            case 4:
                counterView.setText("Ready?");
                break;
            default:
                counterView.setText( "" + (value-1));
        }

        counterView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        arena.addView(counterView);

        counterView.animate()
                .alpha(0f)
                .setDuration(950)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ((ViewGroup) counterView.getParent()).removeView(counterView);
                    }
                });
    }

    private void performReadyGameCountDown(){
        logError("starting start game countdown timer");
        if(gameCounter != null) gameCounter.cancel(); gameCounter = null;

        ((RelativeLayout) findViewById(R.id.space)).setClickable(true);
        final CountDownTimer NgameCounter = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(Math.round(millisUntilFinished/1000)>=1) addCountDownViewForValue(Math.round(millisUntilFinished / 1000));
                if((Math.round(millisUntilFinished/1000)==1)) startGame();
            }
            public void onFinish() {
                // if(!stopGame) startGame();
            }
        };

        gameCounter = NgameCounter;
        gameCounter.start();
    }
    public void logError(String err){
        Log.d("COMET_SMASH ", err);
    }
    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

}
