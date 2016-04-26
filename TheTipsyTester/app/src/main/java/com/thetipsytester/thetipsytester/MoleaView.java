package com.thetipsytester.thetipsytester;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/*
 * Created by chrisenck on 4/24/16.
 */

public class MoleaView extends View {

    public static final int MOLEA_SIZE = 100;        // The size of the moleas
    public static final int MOLEA_MARGIN = 5;        // The space between Moleas

    boolean animate;
    boolean animating;
    boolean isKilled;

    public void animateMoleaPop(){
        this.setRedShape();
        animate = false;

    }

    public void spawn(){
        this.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimation();
                    }
                });
    }
    public void pop(){
        if(!isKilled) isKilled = true;
        this.animate()
                .alpha(0.0f)
                .scaleX((float) 1.55)
                .scaleY((float) 1.55)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        removeSelf();
                    }
                });
    }

    public void removeSelf(){
        ((ViewGroup)this.getParent()).removeView(this);
    }
    public void setRedShape(){
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius((MOLEA_SIZE / 2));
        shape.setColor(Color.RED);
        this.setBackground(shape);
    }
    public MoleaView(Context context) {
        super(context);

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius((MOLEA_SIZE / 2));
        shape.setColor(Color.BLACK);
        this.setBackground(shape);
    }

    public int getBattleFieldSlots(int size){
        return (size / ((MOLEA_SIZE / 3) + MOLEA_MARGIN));
    }

    public int getSlotFromMargin(int margin){
        return (margin / ((MOLEA_SIZE / 3) + MOLEA_MARGIN));
    }

    public RelativeLayout.LayoutParams getMoleaPlacement(int top, int left){

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MOLEA_SIZE, MOLEA_SIZE);
        params.leftMargin = (top * ((MOLEA_SIZE / 3) + MOLEA_MARGIN));
        params.topMargin = (left * ((MOLEA_SIZE / 3) + MOLEA_MARGIN));
        return params;
    }
    public void startAnimation(){
        animate = true;
        if(!animating) fadeMoleaIn();
    }

    public void stopAnimation(){
        animate = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
          setMeasuredDimension(MOLEA_SIZE, MOLEA_SIZE);
    }

    public void fadeMoleaIn(){
        animating = true;
        this.animate()
                .alpha(.75f)
                .scaleX((float) 1.15)
                .scaleY((float) 1.15)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animate) fadeMoleaOut();
                        else{
                            animating = false;
                            pop();
                        }
                    }
                });
    }
    public void fadeMoleaOut(){
        animating = true;
        this.animate()
                .alpha(1.0f)
                .scaleX((float) 1)
                .scaleY((float) 1)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(animate) fadeMoleaIn();
                        else{
                            animating = false;
                            pop();
                        }
                    }
                });
    }
}
