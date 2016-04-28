package com.thetipsytester.thetipsytester;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by chrisenck on 4/26/16.
 */


enum Target {
    TopWall, LeftWall, BottomWall, RightWall
}
enum Direction {
    LEFT, RIGHT, UP, DOWN, UNSET
}

class Tuple<X, Y> {
    public X x;
    public Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

public class cometView extends FrameLayout {

    private static final SecureRandom random = new SecureRandom();

    static int COMET_SIZE = 80;
    static int TRIANGLE_COUNT = 20;

    RelativeLayout.LayoutParams lParams;
    Animation animatorTarget;

    Integer speed = 20;

    Integer X_MAX;
    Integer X_MIN = 5;

    Integer Y_MAX;
    Integer Y_MIN = 5;

    ArrayList<triangle> angles = new ArrayList<triangle>();


    Tuple<Integer,Integer> currentLoc;
    Tuple<Integer,Integer> destinationLoc;

    Tuple<Direction,Direction> heading;
    Target target;

    Integer hitCount = 5;

    static final String STATE_GAME_INTENT_ROCK_THROW = "CometSmashGameBreakRock";
    static final String STATE_GAME_INTENT_END_GAME = "CometSmashGameEndGame";

    private void sendBreakRockMessage() {
        logError("Broadcasting Break Rock message");
        LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(new Intent(STATE_GAME_INTENT_ROCK_THROW));
    }
    private void sendEndGameMessage() {
        logError("Broadcasting End Game message");
        LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(new Intent(STATE_GAME_INTENT_END_GAME));
    }


    public cometView(Context context) {
        super(context);
        lParams = new RelativeLayout.LayoutParams(COMET_SIZE, COMET_SIZE);
        setLayoutParams(lParams);
        setBackgroundColor(Color.BLUE);
        for(int i = 0; i<TRIANGLE_COUNT; i++){
            triangle tmp = new triangle(context);
            addView(tmp);
            angles.add(tmp);
        }
        heading = new Tuple<>(Direction.UNSET,Direction.UNSET);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBreakRockMessage();
                hitCount--;
                if (hitCount == 0) sendEndGameMessage();
            }
        });

        logError("created comet view");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        X_MAX = ((ViewGroup)this.getParent()).getWidth() - (COMET_SIZE+X_MIN);
        Y_MAX = ((ViewGroup)this.getParent()).getHeight()- (COMET_SIZE+Y_MIN);
        logError("comet view has focus with X_MAX: " + X_MAX + "  Y_MAX: " + Y_MAX);

        lParams.topMargin = (Y_MAX-COMET_SIZE)/2;
        lParams.leftMargin = (X_MAX-COMET_SIZE)/2;

        setLayoutParams(lParams);

        if (hasFocus){
            for(int i = 0; i<angles.size(); i++){
                triangle tmp = angles.get(i);
                if(tmp.ra == null) {
                    long angle = 360;
                  //  if((i % 3) == 0) angle = -360;
                    tmp.initSpin(angle, (long)(500*(i*((float)3/(float)angles.size()))));
                }
            }
        }
    }
    public void destroy(){
        stopSpin();
        while(angles.size()>0) angles.remove(0).removeSelf();
    }

    public void stopSpin(){
        for(int i=0; i<angles.size(); i++) angles.get(i).stopSpin();
    }

    private void logError(String err){
        Log.d("COMET_VIEW ", err);
    }


    public void initRandomTrajectory(){
        logError("Random Trajectory sent");
        currentLoc = new Tuple<>(lParams.leftMargin,lParams.topMargin);
        if(animatorTarget != null) {
            animatorTarget.cancel();
            animatorTarget = null;
        }
        setRandomDestination();
        setRandomTarget();
        setWallForTarget();
        validateNoLines();
        setHeading();
        launchComet();
    }

    private void launchComet(){

 /*       animate()
                .translationX(Math.abs(destinationLoc.x-currentLoc.x))
                .translationY(Math.abs(destinationLoc.y-currentLoc.y))
                .setDuration(calcTravelTime())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        setNewDestination();
                        launchComet();
                    }
                }).start();
*/
        if(animatorTarget != null) {
            animatorTarget.cancel();
            animatorTarget = null;
        }

        animatorTarget = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                lParams.leftMargin = (int)(destinationLoc.x * interpolatedTime);
                lParams.topMargin = (int)(destinationLoc.y * interpolatedTime);
                setLayoutParams(lParams);
            }
        };
          animatorTarget.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                lParams.leftMargin = destinationLoc.x;
                lParams.topMargin = destinationLoc.y;
                setLayoutParams(lParams);
                setNewDestination();
                launchComet();
            }
        });

        animatorTarget.setInterpolator(this.getContext(), android.R.anim.linear_interpolator);
        animatorTarget.setDuration(calcTravelTime()); // in ms
        animatorTarget.setFillEnabled(true);
        animatorTarget.setFillAfter(true);
        startAnimation(animatorTarget);

    }

    private Double calculateImpactAngle(){
        logError("Calculating Impact angle current Y: "+currentLoc.y+" current X: "+currentLoc.x+" destination Y: "+destinationLoc.y+" destination X: "+destinationLoc.x);
        return Math.atan(Math.abs(currentLoc.y - destinationLoc.y)/Math.abs(currentLoc.x-destinationLoc.x));
    }

    private void setNewDestination(){

        setNewHeading();
        Double angleB = calculateImpactAngle();
        Double angleC = 360 - (90+angleB);

        switch (heading.x){
            case LEFT: calcTargetForLeft(angleB, angleC);
                break;
            case RIGHT: calcTargetForRight(angleB,angleC);
                break;
        }

        validateNoLines();
        setNewTarget();
    }

    private long calcTravelTime(){
        return (long)(Math.sqrt(Math.abs(currentLoc.x-destinationLoc.x)+Math.abs(currentLoc.y-destinationLoc.y))*(1000/speed));
    }

    private void calcTargetForLeft(Double angleB, Double angleC) {

        Double newY = calcBlowOutForValue(destinationLoc.x, X_MIN, angleB, angleC);
        Double newX = 0.0;

        if (!(newY < Y_MAX)) {
            newY = 0.0;
            newX = calcBlowOutForValue(destinationLoc.y, Y_MIN, angleB, angleC);
        }
        currentLoc = destinationLoc;
        destinationLoc = new Tuple<>(newX.intValue(), newY.intValue());
    }

    private void calcTargetForRight(Double angleB, Double angleC) {

        Double newY = calcBlowOutForValue(X_MAX, destinationLoc.x, angleB, angleC);
        Double newX = 0.0;

        if (!(newY < Y_MAX)) {
            newY = 0.0;
            newX = calcBlowOutForValue(Y_MAX, destinationLoc.y, angleB, angleC);
        }
        currentLoc = destinationLoc;
        destinationLoc = new Tuple<>(newX.intValue(), newY.intValue());
    }

    private Double calcBlowOutForValue(Integer v, Integer v_min, Double angleB, Double angleC){
      return ((v-v_min)*Math.sin(angleC))/Math.sin(angleB);
   }

    private void setNewTarget(){
        if(destinationLoc.x == X_MIN) target = Target.LeftWall;
        if(destinationLoc.x == X_MAX) target = Target.RightWall;
        if(destinationLoc.y == Y_MIN) target = Target.TopWall;
        if(destinationLoc.y == Y_MAX) target = Target.BottomWall;
        logError("Target "+ target);
    }

    private void setNewHeading(){
        switch (this.target){
            case LeftWall: heading.x = Direction.LEFT;
                break;
            case RightWall:  heading.x = Direction.RIGHT;
                break;
            case TopWall:  heading.y = Direction.DOWN;
                break;
            case BottomWall:  heading.y = Direction.UP;
                break;
        }
        logError("New Heading ( " +heading.x + " , " + heading.y+" )");
    }

    private void setHeading(){

        if(destinationLoc.y<currentLoc.y){ heading.y = Direction.UP;}
        else{ heading.y = Direction.DOWN;}

        if(destinationLoc.x<currentLoc.x){ heading.x = Direction.LEFT;}
        else{ heading.x = Direction.RIGHT;}
        logError("Heading ( " +heading.x + " , " + heading.y+" )");

    }

    private void setWallForTarget(){
        switch (this.target){
            case LeftWall: this.destinationLoc.x = X_MIN;
                break;
            case RightWall: this.destinationLoc.x = X_MAX;
                break;
            case TopWall: this.destinationLoc.y = Y_MIN;
                break;
            case BottomWall: this.destinationLoc.y = Y_MAX;
                break;
        }
        logError("Set to wall, new destination is X: " + destinationLoc.x + "  Y: " + destinationLoc.y);
    }

    private void setRandomDestination(){
        destinationLoc = new Tuple<>(random.nextInt(X_MAX),random.nextInt(Y_MAX));
        if(destinationLoc.y <= Y_MIN) destinationLoc.y = Y_MIN;
        if(destinationLoc.x <= X_MIN) destinationLoc.x = X_MIN;
        logError("Random destination X: " +destinationLoc.x + "  Y: " + destinationLoc.y);
    }

    private void setRandomTarget(){
        target = randomEnum(Target.class);
        logError("Random Target : "+target);
    }

    private void validateNoLines(){
        if(currentLoc.x == destinationLoc.x){
            logError("Line found, correcting ");
            if((destinationLoc.x + 2) < X_MAX ) destinationLoc.x+=2;
            else destinationLoc.x-=2;
        }
        if(currentLoc.y == destinationLoc.y){
            logError("Line found, correcting ");
            if((destinationLoc.y + 2) < Y_MAX ) destinationLoc.y+=2;
            else destinationLoc.y-=2;
        }

     //   if((destinationLoc.x == X_MAX)&&(destinationLoc.y == Y_MAX)){ destinationLoc.y=-2; validateNoLines();}
     //   else if((destinationLoc.x == X_MIN)&&(destinationLoc.y == Y_MAX)){ destinationLoc.x=+2; validateNoLines();}
     //   else if((destinationLoc.x == X_MAX)&&(destinationLoc.y == Y_MIN)){ destinationLoc.y=+2; validateNoLines();}
     //   else if((destinationLoc.x == X_MIN)&&(destinationLoc.y == Y_MIN)){ destinationLoc.x=+2; validateNoLines();}

    }

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}

class triangle extends View {

    static final String STATE_GAME_INITENT_ROCK_THROW = "CometSmashGameBrekRock";
    static int COMET_SIZE = 50;
    RotateAnimation ra;

    public triangle(Context context){
        super(context);
        FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(COMET_SIZE,COMET_SIZE, Gravity.CENTER);
        setLayoutParams(l);
    }

    public void initSpin(long d, long delay){

        ra = new RotateAnimation(0, d, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setInterpolator(this.getContext(), android.R.anim.linear_interpolator);
        ra.setDuration(500);
        ra.setFillAfter(true);
        ra.setRepeatCount(Animation.INFINITE);
       new android.os.Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               try {
                   startAnimation(ra);
               } catch (Exception ex) {
               }
           }
       }, delay);

    }


    public void stopSpin(){
        ra.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
    }

    private void drawBackground(Canvas canvas) {
        int width = (int) (COMET_SIZE+5);
        int height = (int) COMET_SIZE;
        int mPointHeight = (int) COMET_SIZE;
        int mPointedHeight = (int) COMET_SIZE;

        Point a = new Point(0, 0);
        Point b = new Point(width, 0);
        Point c = new Point(width, height - mPointHeight);
        Point d = new Point((width/2)+(mPointedHeight/2), height - mPointHeight);
        Point e = new Point((width/2), height);
        Point f = new Point((width/2)-(mPointedHeight/2), height - mPointHeight);
        Point g = new Point(0, height - mPointHeight);

        Path path = new Path();
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.lineTo(d.x, d.y);
        path.lineTo(e.x, e.y);
        path.lineTo(f.x, f.y);
        path.lineTo(g.x, g.y);

        Paint np = new Paint();
        np.setColor(Color.BLACK);

        canvas.drawPath(path, np);
    }

    public void removeSelf(){
        ((ViewGroup)this.getParent()).removeView(this);
    }

    private void logError(String err){
        Log.d("COMET_VIEW_TRIANGLE ", err);
    }
}