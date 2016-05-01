package com.thetipsytester.thetipsytester;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Handler;

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

    boolean debug = false;

    private static final SecureRandom random = new SecureRandom();

    int speed = 50; // these 3 values are replaced, so they don't really matter
    int COMET_SIZE = 100;
    int TRIANGLE_COUNT = 20;

    RelativeLayout.LayoutParams lParams;
    public SlideTranslate animatorTarget;


    Integer X_MAX;
    Integer X_MIN = 5;

    Integer Y_MAX;
    Integer Y_MIN = 5;

    ArrayList<triangle> angles = new ArrayList<triangle>();
    //ArrayList<triangle> hiddenAngles = new ArrayList<triangle>();


    Tuple<Integer,Integer> currentLoc;
    Tuple<Integer,Integer> destinationLoc;

    Tuple<Direction,Direction> heading;
    Target target;

    Integer hitCount = 7;

    boolean debris;
    boolean inverted;

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

    private int scaleValue(int value){
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }


    public cometView(Context context, int triangleCount, int size, int comet_speed, boolean invert, boolean isDebris) {
        super(context);
        speed = comet_speed;
        TRIANGLE_COUNT = triangleCount;
        COMET_SIZE = scaleValue(size);
        inverted = invert;
        debris = isDebris;

        lParams = new RelativeLayout.LayoutParams(COMET_SIZE, COMET_SIZE);
        setLayoutParams(lParams);
        lParams.topMargin = -COMET_SIZE*2;
        lParams.leftMargin = -COMET_SIZE*2;
       // setBackgroundColor(Color.BLUE);
        for(int i = 0; i<TRIANGLE_COUNT; i++){
            triangle tmp = new triangle(context, COMET_SIZE-scaleValue(20));
            addView(tmp);
            angles.add(tmp);
        }
        heading = new Tuple<>(Direction.UNSET,Direction.UNSET);

        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBreakRockMessage();
                hitCount--;
                if (hitCount == 0) sendEndGameMessage();
            }
        });*/

        logError("created comet view");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    public void forceRotation(){

        for(int i = 0; i<angles.size(); i++){
            triangle tmp = angles.get(i);
            if(tmp.ra == null) {
                long angle = 360;
                if((i % 2) == 0 && inverted) angle = -360;
                tmp.initSpin(angle, (long)(300*(i*((float)3/(float)angles.size()))));
            }
        }

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            X_MAX = ((ViewGroup)this.getParent()).getWidth() - (COMET_SIZE + X_MIN);
            Y_MAX = ((ViewGroup) this.getParent()).getHeight()- (COMET_SIZE+Y_MIN);
            logError("comet view has focus with X_MAX: " + X_MAX + "  Y_MAX: " + Y_MAX);

            forceRotation();
        }
    }
    /*public void destroy(){
     super
        //stopSpin();
      //  while(angles.size()>0) angles.remove(0).removeSelf();
    }*/

    public void stopSpin(){
        for(int i=0; i<angles.size(); i++) angles.get(i).removeSelf();
    }

    private void logError(String err){
        if(debug) Log.d("COMET_VIEW ", err);
    }


    public void initRandomTrajectory(Integer left, Integer top){
        currentLoc = new Tuple<>(left,top);
        logError("Random Trajectory sent for current loc X= " + currentLoc.x+" Y= "+currentLoc.y);
       /* if(animatorTarget != null) {
            animatorTarget.cancel();
            animatorTarget = null;
        }*/
        setRandomDestination();
        setRandomTarget();
        setWallForTarget();
        validateNoLines();
        setHeading();
        launchComet();
    }

    private void launchComet(){

  /*      RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(COMET_SIZE,COMET_SIZE);
        nlp.leftMargin = (currentLoc.x );
        nlp.topMargin = (currentLoc.y );
        logError("Setting location post animation || X= " + lParams.leftMargin + " Y=" + lParams.topMargin);
        setLayoutParams(nlp);
*/
        float deltaX=destinationLoc.x - currentLoc.x;
        float deltaY= destinationLoc.y - currentLoc.y;


       // animatorTarget = new SlideTranslate(currentLoc.x,destinationLoc.x,currentLoc.y,destinationLoc.y);
        animatorTarget = new SlideTranslate(0,deltaX,0,deltaY);
        animatorTarget.fromX = currentLoc.x;
        animatorTarget.fromY = currentLoc.y;

        animatorTarget.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // setNewDestination();
                //launchComet();

                android.os.Handler mainHandler = new android.os.Handler(getContext().getMainLooper());

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(COMET_SIZE, COMET_SIZE);
                        nlp.leftMargin = (destinationLoc.x);
                        nlp.topMargin = (destinationLoc.y);
                        logError("Setting location post animation || X= " + lParams.leftMargin + " Y=" + lParams.topMargin);
                        setLayoutParams(nlp);
                        lParams = nlp;
                        if (!debris) initRandomTrajectory(destinationLoc.x, destinationLoc.y);
                        else killComet();
                        // setNewDestination();
                        // launchComet();
                    }
                };
                mainHandler.post(runnable);

            }
        });
        Double time = Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));
        logError("'Time' factor = " + time);
        animatorTarget.setDuration(time.longValue());
        animatorTarget.setInterpolator(new LinearInterpolator());
        animatorTarget.setFillAfter(true);
    //    animatorTarget.setDuration(calcTravelTime()); // in ms
        startAnimation(animatorTarget);

    }

    private void killComet(){
       // while (angles.size()>0) angles.get(0).removeSelf();
        ((ViewGroup)this.getParent()).removeView(this);
    }

    public void removeChunks(){
        if(angles.size()>4){
            for(int i = 0; i<3; i++) removeView(angles.remove(0));
        }

    }
    private Double calculateImpactAngle(){
        logError("Calculating Impact angle current Y: "+currentLoc.y+" current X: "+currentLoc.x+" destination Y: "+destinationLoc.y+" destination X: "+destinationLoc.x);
        return Math.atan(Math.abs(currentLoc.y - destinationLoc.y)/Math.abs(currentLoc.x-destinationLoc.x));
    }

    private void setNewDestination(){

        setNewHeading();
        Double angleB = calculateImpactAngle()*360;
        Double angleC = 360 - (90+angleB);
        logError("Impact angleB : "+angleB +" AngleC : "+angleC);
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
        return (long)(Math.sqrt(Math.abs(currentLoc.x-destinationLoc.x)+Math.abs(currentLoc.y-destinationLoc.y))*(speed/100));
    }

    private void calcTargetForLeft(Double angleB, Double angleC) {

        Double newY = calcBlowOutForValue(destinationLoc.x, X_MIN, angleB, angleC);
        Double newX = 0.0;

        if (!(newY < Y_MAX)) {
            newY = 0.0;
            newX = calcBlowOutForValue(destinationLoc.y, Y_MIN, angleB, angleC);
        }
        logError("Destination location for left || X = "+ newX +" Y = "+newY);
        currentLoc = destinationLoc;
        destinationLoc = new Tuple<>(newX.intValue(), newY.intValue());
    }

    private void calcTargetForRight(Double angleB, Double angleC) {

        Double newY = calcBlowOutForValue(X_MAX, destinationLoc.x, angleB, angleC);
        Double newX = X_MIN.doubleValue();

        if (!(newY < Y_MAX)) {
            newY = Y_MIN.doubleValue();
            newX = calcBlowOutForValue(Y_MAX, destinationLoc.y, angleB, angleC);
        }
        if ((newY < Y_MIN)) {
            newY = Y_MIN.doubleValue();
            newX = calcBlowOutForValue(Y_MAX, destinationLoc.y, angleB, angleC);
        }

        if ((newX > X_MAX)) {
            newX = X_MAX.doubleValue();
        }

        if ((newX < X_MIN)) {
            newX = X_MIN.doubleValue();
        }

        logError("Destination location for left || X = "+ newX +" Y = "+newY);
        currentLoc = destinationLoc;
        destinationLoc = new Tuple<>(newX.intValue(), newY.intValue());
    }

    private Double calcBlowOutForValue(Integer v, Integer v_min, Double angleB, Double angleC){
      return (Math.abs(v-v_min)*Math.sin(angleC))/Math.sin(angleB);
   }

    private void setNewTarget(){
        if(destinationLoc.x == X_MIN) target = Target.LeftWall;
        if(destinationLoc.x == X_MAX) target = Target.RightWall;
        if(destinationLoc.y == Y_MIN) target = Target.TopWall;
        if(destinationLoc.y == Y_MAX) target = Target.BottomWall;
        logError("New Target "+ target);
    }

    private void setNewHeading(){
        switch (this.target){
            case LeftWall: heading.x = Direction.RIGHT;
                break;
            case RightWall:  heading.x = Direction.LEFT;
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
        logError("Values before setting heading || current Y: "+currentLoc.y+" current X: "+currentLoc.x+" destination Y: "+destinationLoc.y+" destination X: "+destinationLoc.x);
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
            if((destinationLoc.x + 5) < X_MAX ) destinationLoc.x+=5;
            else destinationLoc.x-=5;
        }
        if(currentLoc.y == destinationLoc.y){
            logError("Line found, correcting ");
            if((destinationLoc.y + 5) < Y_MAX ) destinationLoc.y+=5;
            else destinationLoc.y-=5;
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
    int COMET_SIZE = 80;
    RotateAnimation ra;


    public triangle(Context context, int size){
        super(context);
        COMET_SIZE = size;
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
      if(ra !=null) ra.cancel();
        else logError("rotation animation was null");
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
        stopSpin();
        ((ViewGroup)this.getParent()).removeView(this);
    }

    private void logError(String err){
        Log.d("COMET_VIEW_TRIANGLE ", err);
    }
}

class SlideTranslate extends TranslateAnimation{

    float fromX;
    float fromY;
    float toX;
    float toY;

   public Tuple<Long,Long> currentLoc;

    public SlideTranslate(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
        fromX = fromXDelta;
        toX = toXDelta;
        fromY = fromYDelta;
        toY = toYDelta;
        currentLoc = new Tuple<>(0l,0l);
    }

    @Override
    protected void applyTransformation (float interpolatedTime, Transformation t){
        super.applyTransformation(interpolatedTime,t);
        currentLoc.x = (long)(fromX + (interpolatedTime * toX));
        currentLoc.y = (long)(fromY + (interpolatedTime * toY));
    }

}
