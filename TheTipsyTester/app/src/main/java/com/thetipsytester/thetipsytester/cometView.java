package com.thetipsytester.thetipsytester;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by chrisenck on 4/26/16.
 */


enum Target {
    TopWall, LeftWall, BottomWall, RightWall
}
enum Direction {
    LEFT, RIGHT, UP, DOWN
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

    Integer X_MAX;
    Integer X_MIN;

    Integer Y_MAX;
    Integer Y_MIN;

    ArrayList<triangle> angles = new ArrayList<triangle>();


    Tuple<Integer,Integer> currentLoc;
    Tuple<Integer,Integer> destinationLoc;

    Tuple<Direction,Direction> heading;
    Target target;


    public cometView(Context context) {
        super(context);
        setLayoutParams(new LayoutParams(COMET_SIZE, COMET_SIZE, Gravity.CENTER));
        setBackgroundColor(Color.BLUE);
        for(int i = 0; i<TRIANGLE_COUNT; i++){
            triangle tmp = new triangle(context);
            addView(tmp);
            angles.add(tmp);
        }
        logError("created comet view");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        logError("Layout comet view");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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

    public void stopSpin(){
        for(int i=0; i<angles.size(); i++) angles.get(i).stopSpin();
    }

    public void logError(String err){
        Log.d("COMET_VIEW ", err);
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    private void setHeading(){

        if(destinationLoc.y<currentLoc.y){ heading.y = Direction.UP;}
        else{ heading.y = Direction.DOWN;}

        if(destinationLoc.x<currentLoc.x){ heading.x = Direction.LEFT;}
        else{ heading.x = Direction.RIGHT;}
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
    }

    private void setRandomTarget(){
        this.target = randomEnum(Target.class);
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}

class triangle extends View {

    static final String STATE_GAME_INITENT_ROCK_THROW = "CometSmashGameBrekRock";
    static int COMET_SIZE = 50;
    RotateAnimation ra;

    private void sendBreakRockMessage() {
        logError("Broadcasting message");
        Intent intent = new Intent(STATE_GAME_INITENT_ROCK_THROW);
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this.getContext()).sendBroadcast(intent);
    }


    public triangle(Context context){
        super(context);
        FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(COMET_SIZE,COMET_SIZE, Gravity.CENTER);
      //  l.leftMargin = (int)Math.round((200-COMET_SIZE)/2);
      //  l.topMargin = (int)Math.round((200-COMET_SIZE)/2);
        setLayoutParams(l);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBreakRockMessage();
                setVisibility(INVISIBLE);
            }
        });
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
                try{
                    startAnimation(ra);
                }catch (Exception ex){}
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
    public void logError(String err){
        Log.d("COMET_VIEW_TRIANGLE ", err);
    }
}