package com.thetipsytester.thetipsytester.moveBall;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.thetipsytester.thetipsytester.moveBall.Ball;
import com.thetipsytester.thetipsytester.moveBall.Game;
import com.thetipsytester.thetipsytester.moveBall.Hole;

public class DrawingPanel extends View {
	
	private Canvas drawCanvas;
	private	Bitmap canvasBitmap;

	public DrawingPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
		canvasBitmap.eraseColor(Color.WHITE);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, null);
	}

	public void update(Game game) {
		canvasBitmap.eraseColor(Color.WHITE);
		Paint textPaint = new Paint();
		textPaint.setColor(Color.GRAY);
		textPaint.setTextSize(18);
		textPaint.setAntiAlias(true);
		
		Paint holePaint = new Paint();
		int color = Color.CYAN;
		holePaint.setAntiAlias(true);
		
		int count = 1;
		for(Hole h : new LinkedList<Hole>(game.getField().getHoles())){
			holePaint.setColor(color);
			drawCanvas.drawCircle(h.getX(), h.getY(), Hole.RADIUS, holePaint);
			drawCanvas.drawText(""+count, h.getX() - (Hole.RADIUS/2), h.getY() + (Hole.RADIUS/2), textPaint);
			color += 10;
			count ++;
		}
		
		Paint ballPaint = new Paint();
		ballPaint.setColor(Color.RED);
		ballPaint.setAntiAlias(true);
		drawCanvas.drawCircle(game.getField().getBall().getX(), game.getField().getBall().getY(), Ball.RADIUS, ballPaint);
		this.invalidate();
	}
	
}
