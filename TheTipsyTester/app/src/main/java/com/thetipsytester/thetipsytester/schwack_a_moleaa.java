package com.thetipsytester.thetipsytester;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;


public class schwack_a_moleaa extends AppCompatActivity {

    public static final long MOLEA_GAME_TIMER = 20000; // The game time in milisec's

    static final String STATE_GAME_TIME = "schwachGameTime";
    static final String STATE_GAME_SCORE = "schwackGameScore";

    static final String STATE_GAME_SAFE_X_ARRAY = "schwackGameSafeX";
    static final String STATE_GAME_SAFE_Y_ARRAY = "schwackGameSafeY";

    public int score;
    public long gameClock;
    public CountDownTimer gameCounter;


    public boolean safeX[];
    public boolean safeY[];

    boolean animate;
    boolean animating;
    boolean stopGame;

    AlertDialog testA;

    public int borderBuffer = 4;

    Random r = new Random();
    ArrayList<MoleaView> oldMoleas = new ArrayList<MoleaView>();
    ArrayList<MoleaView> activeMoleas = new ArrayList<MoleaView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            score = 0;
            gameClock = MOLEA_GAME_TIMER;
        }else{
            score = savedInstanceState.getInt(STATE_GAME_SCORE);
            gameClock = savedInstanceState.getLong(STATE_GAME_TIME);
            safeX = savedInstanceState.getBooleanArray(STATE_GAME_SAFE_X_ARRAY);
            safeY = savedInstanceState.getBooleanArray(STATE_GAME_SAFE_Y_ARRAY);
        }

        setContentView(R.layout.activity_schwack_a_moleaa);
        this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#" + PreferenceManager.getDefaultSharedPreferences(this).getString("color", "232323")));
        logError("Create teh Schwack-a-mole game!!");

    }

    public int getSafeXLocation(){
        int val = r.nextInt((safeX.length - borderBuffer) + (borderBuffer/2));
        logError("Getting safe x= "+val);
        int breakFlag = 5;
        while(!isSafeLocation(safeX ,val)){
            val = r.nextInt((safeX.length - borderBuffer) + (borderBuffer/2));
            logError("recheck safe x= "+val);
            if(breakFlag>0)breakFlag--;
            else break;
        }
        return val;
    }

    public boolean isSafeLocation(boolean arr[], int loc){
        for(int i = loc; i<(loc+3); i++){
            if(arr[i]){
                return false;
            }
        }
        return true;
    }

    public int getSafeYLocation(){
        int breakFlag = 5;

        int val = r.nextInt((safeY.length - borderBuffer) + (borderBuffer/2));
        logError("Getting safe y= "+val);
        while(!isSafeLocation(safeY ,val)){
            val = r.nextInt((safeY.length - borderBuffer) + (borderBuffer/2));
            logError("recheck safe y= "+val);
            if(breakFlag>0)breakFlag--;
            else break;
        }
        return val;
    }

    public void setUsedLocation(int x, int y){
        for(int i=(x); i<(x+3); i++){
            safeX[i] = true;
        }
        for(int i=(y); i<(y+3); i++){
            safeY[i] = true;
        }
    }

    public void removeUsedLocation(int x, int y){
        for(int i=(x); i<(x+3); i++){
            safeX[i] = false;
        }
        for(int i=(y); i<(y+3); i++){
            safeY[i] = false;
        }
    }

    public void addRecycledMoleas(MoleaView view){
        activeMoleas.remove(view);
      //  oldMoleas.add(view);
    }

    public MoleaView getRecycledMoleas() {
        if (true) {
    //        if (oldMoleas.size() == 0) {
            MoleaView m = setOnMoleaSmashed(new MoleaView(findViewById(R.id.molea_arena).getContext()));
            activeMoleas.add(m);
            return m;
        }else{
            logError("Recycling Molea");
            MoleaView m =  oldMoleas.remove(0);
            activeMoleas.add(m);
            return m;
        }
    }

    public void moleaManager(){

        while(activeMoleas.size()<3){
            generateMolea();
        }

    }

    public void generateMolea(){

        logError("Make teh molea!!");

        final MoleaView molea = getRecycledMoleas();
        logError("Just got the recycled molea");
        int x = getSafeXLocation();
        int y  = getSafeYLocation();

        logError("Got safe loc at X= "+ x + " Y= "+y);
        setUsedLocation(x, y);
        logError("Set safe");

        molea.setAlpha(0.0f);
        molea.setScaleX(0.0f);
        molea.setScaleY(0.0f);
        ((RelativeLayout) findViewById(R.id.molea_arena)).addView(molea, molea.getMoleaPlacement(x, y));
        logError("Added molea to view,about to spawn");
        molea.spawn();
    }

    public MoleaView setOnMoleaSmashed(final MoleaView molea){
        molea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        molea.setEnabled(false);
                        if (!molea.isKilled) {
                            logError("About to pop molea");
                            molea.animateMoleaPop();
                            logError("About to recycle molea");
                            addRecycledMoleas(molea);
                            logError("creating molea");
                            generateMolea();
                            logError(" Increment score");
                            incrementScore();
                        }

                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) molea.getLayoutParams();
                        removeUsedLocation(molea.getSlotFromMargin(lp.leftMargin), molea.getSlotFromMargin(lp.topMargin));

                    }
                });
            }
        });
        return molea;
    }



    public void incrementScore(){
        this.score++;
        setScoreBoard();
    }

    private void setScoreBoard(){
        try{
            ((TextView)findViewById(R.id.scoreBoard)).setText("" + score + "");
        }catch (Exception ex){};
    }

    private void setGameClockBoard(){
        if( gameClock < 5000 ){
            animate = true;
            ((TextView)findViewById(R.id.timerBoard)).setTextColor(Color.RED);
            if(!animating) sizeIn(((TextView)findViewById(R.id.timerBoard)));
        }
        ((TextView)findViewById(R.id.timerBoard)).setText("" + ((double) gameClock / 1000.0));
    }

    public void sizeIn(final TextView view){
        animating = true;
        view.animate()
                .scaleX((float) 1.15)
                .scaleY((float) 1.15)
                .setDuration(330)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animate) sizeOut(view);
                        else animating = false;
                    }
                });
    }
    public void sizeOut(final TextView view){
        animating = true;
        view.animate()
                .scaleX((float) 1)
                .scaleY((float) 1)
                .setDuration(330)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animate) sizeIn(view);
                        else animating = false;
                    }
                });
    }

    private void addCountDownViewForValue(final int value){
        RelativeLayout arena = (RelativeLayout)findViewById(R.id.masterView);
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


    private void startGameCountDown(long time){
        logError("starting Game timer");

        MoleaView molea = new MoleaView(this);
        RelativeLayout arena = (RelativeLayout) findViewById(R.id.molea_arena);

        safeX = new boolean[molea.getBattleFieldSlots(arena.getWidth())];
        safeY = new boolean[molea.getBattleFieldSlots(arena.getHeight())];


        gameCounter = new CountDownTimer(time, 10) {
            public void onTick(long millisUntilFinished) {
                gameClock = millisUntilFinished;
                setGameClockBoard();
            }
            public void onFinish() {
                if(!stopGame) endGame();
            }
        }.start();
        moleaManager();
    }


    private void startGame() {
        logError("STARTIN TEH SCHWACKIN GAME!!!!");
        startGameCountDown(gameClock);
        setScoreBoard();
    }

    private void endGame(){
        logError("END GAMEEEEE!!!!");

        Intent currentIntent = getIntent();
        Intent intent = new Intent(this, scorereportActivity.class);

        intent.putExtra("prevTest", "schwack"); // this needs to be updated
        intent.putStringArrayListExtra("nextTests", currentIntent.getStringArrayListExtra("nextTests"));
        intent.putExtra("score", score);
        intent.putExtra("calibration", currentIntent.getBooleanExtra("calibration", false));
        intent.putExtra("BAC", currentIntent.getDoubleExtra("BAC", 0));
        intent.putExtra("bacCount", currentIntent.getIntExtra("bacCount", 0));
        intent.putExtra("numTests", currentIntent.getIntExtra("numTests", 0));

        System.out.println("bacCount: " + currentIntent.getIntExtra("bacCount", 0) + "\n\n\n");

        startActivity(intent);
        finish();
    }

    private void testAlert(){
       AlertDialog.Builder test = new AlertDialog.Builder(this);
        test.setTitle("Schwack-A-Molea Test").setMessage("Schwack as many Moleas as you can before the timer goes off!. \n\nGood Luck!!");
        test.setCancelable(false).setPositiveButton("Start", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                performReadyGameCountDown();
            }
        });
        testA = test.create();
        testA.show();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        logError("Changed configs!");
    }

    @Override
    public void onPause(){
        if(gameCounter != null) gameCounter.cancel(); gameCounter = null;
        logError("Pausin teh schwackin");
        stopGame = true;
        if(testA != null) testA.cancel(); testA = null;

        while (activeMoleas.size()>0) {
            MoleaView molea = activeMoleas.remove(0);
            ((ViewGroup)molea.getParent()).removeView(molea);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) molea.getLayoutParams();
            removeUsedLocation(molea.getSlotFromMargin(lp.leftMargin), molea.getSlotFromMargin(lp.topMargin));
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        logError("Resumed schwackin");
        stopGame = false;
        if(gameClock != MOLEA_GAME_TIMER) performReadyGameCountDown();
        else testAlert();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
       // gameCounter.cancel();
        logError("SAVED INSTANCE STATE FOR THE SCHWACK");
        animate = false;
        savedInstanceState.putInt(STATE_GAME_SCORE, score);
        savedInstanceState.putLong(STATE_GAME_TIME, gameClock);
        savedInstanceState.putBooleanArray(STATE_GAME_SAFE_X_ARRAY, safeX);
        savedInstanceState.putBooleanArray(STATE_GAME_SAFE_Y_ARRAY, safeY);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy(){
       // gameCounter.cancel();
        logError("Ohhhhh, it's destroyed! That's Schwack!");
        if(gameCounter != null) gameCounter.cancel(); gameCounter = null;
        logError("Pausin teh schwackin");
        stopGame = true;
        if(testA != null) testA.cancel(); testA = null;

        animate = false;
        while (activeMoleas.size()>0) {
            MoleaView molea = activeMoleas.remove(0);
            ((ViewGroup)molea.getParent()).removeView(molea);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) molea.getLayoutParams();
            removeUsedLocation(molea.getSlotFromMargin(lp.leftMargin), molea.getSlotFromMargin(lp.topMargin));
        }
        super.onDestroy();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        score = savedInstanceState.getInt(STATE_GAME_SCORE);
        gameClock = savedInstanceState.getLong(STATE_GAME_TIME);
        safeX = savedInstanceState.getBooleanArray(STATE_GAME_SAFE_X_ARRAY);
        safeY = savedInstanceState.getBooleanArray(STATE_GAME_SAFE_Y_ARRAY);
        logError("RESTORED INSTANCE STATE FOR SCHWACKIN!");

    }

    public void logError(String err){
        Log.d("Schwack-a-molea ",err);
    }
}

