package com.thetipsytester.thetipsytester;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;


public class schwack_a_moleaa extends AppCompatActivity {

    public static final int MOLEA_GAME_TIMER = 20; // The total number of moles given to the player
    public static final int MOLEA_LIFE_TIME = 2;   // The time that the mole stays around
    public static final int MOLEA_MAX_COUNT = 5;   // The max number of moles in the area
    public static final int MOLEA_MIN_COUNT = 1;   // The min number of moles in the area

    public int score;
    public boolean moleManagerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schwack_a_moleaa);

    }

    @Override
    protected void onResume() {
        super.onResume();

        this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#" + PreferenceManager.getDefaultSharedPreferences(this).getString("color", "232323")));

        startMoleaManager((RelativeLayout) findViewById(R.id.molea_arena));
    }


    public void startMoleaManager(RelativeLayout battleField){

        this.generateMolea(battleField);
    }

    public void generateMolea(RelativeLayout battleField){

        MoleaView molea = new MoleaView(this);

        molea = this.setMoleaPlacement(molea);
        molea = this.setOnMoleaSmashed(molea);
        RelativeLayout.LayoutParams lp = molea.getMoleaPlacement(10, 10);
        battleField.addView(molea, lp);
    }

    public MoleaView setMoleaPlacement(MoleaView molea){
       /* molea.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        */

        molea.offsetLeftAndRight(400);
        molea.offsetTopAndBottom(600);


        return molea;
    }

    public MoleaView setOnMoleaSmashed(final MoleaView molea){
        molea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                animateMoleaPop(molea);
                incrementScore();
            }
        });
        return molea;
    }

    /*
    * Methods for on click events, increment score and pop the molea!
    */
    public void animateMoleaPop(MoleaView molea){
        ((ViewGroup)molea.getParent()).removeView(molea);
    }

    public void incrementScore(){
        this.score++;
    }
}

