package com.thetipsytester.thetipsytester.moveBall;

import java.util.Timer;
import java.util.TimerTask;

import com.thetipsytester.thetipsytester.moveBall.AccelerometerHandler;
import com.thetipsytester.thetipsytester.move_ball;

public class Game implements Runnable{
	public static int TIME_LIMIT = 150; // Time limit set to 180 sec
	public static int UPDATES_PER_SECOND = 60;
	private Field field;
	private Chrono chrono;
	private move_ball activity;
	private Timer updateTimer;
	private AccelerometerHandler sensor;
	
	public Game(int fieldHeight, int fieldWidth, move_ball activity){
		field = new Field(fieldHeight, fieldWidth);
		sensor = new AccelerometerHandler(activity);
		chrono = new Chrono();
		chrono.addListener(this);
		this.activity = activity;
		updateTimer = new Timer();
		activity.updateDrawingPanel(this);
	}

	public Field getField() {
		return field;
	}
	
	public void startGame(){
		chrono.start();
		newTimer();
		long delay = 1000/Game.UPDATES_PER_SECOND;
		updateTimer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				update();
			}
			
		}, delay, delay);
	}

	public void pauseGame(){
		chrono.pause();
		newTimer();
	}
	
	public void stopGame(){
		chrono.stop();
		newTimer();
		field = new Field(field.getHeight(), field.getWidth());
		activity.updateDrawingPanel(this);
	}
	
	public void winGame(){
		stopGame();
		activity.showResult(true);
	}
	
	public void loseGame(){
		stopGame();
		activity.showResult(false);
	}
	
	public void update(){
		field.update(-sensor.getAxisX(), sensor.getAxisY());
		int time = chrono.getTimeInSeconds();
		if(time > TIME_LIMIT){
			loseGame();
		}else{
			Hole h = field.getHoles().getFirst();
			if(field.distanceToBall(h) < 5){
				field.getHoles().removeFirst();
				if(field.getHoles().isEmpty())
					winGame();
			}
		}
		activity.updateDrawingPanel(this);
	}

	@Override
	public void run() {
		activity.updateTimerText(chrono.getTimeInSeconds());
	}
	
	private void newTimer(){
		try{
			updateTimer.cancel();
			updateTimer = new Timer();
		}catch(IllegalStateException e){}
	}

}
