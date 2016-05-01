package com.thetipsytester.thetipsytester.moveBall;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Chrono{
	
	private int refreshesPerSecond = 20;
	private Timer timer;
	private long counter;
	private long lastCheckedTime;
	private long lastReportedToListeners;
	private List<Runnable> listeners = new ArrayList<Runnable>();

	public Chrono() {
		timer = new Timer();
	}
	
	public void start(){
		long delay = 1000/refreshesPerSecond;
		lastCheckedTime = Calendar.getInstance().getTimeInMillis();
		newTimer();
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				long currentTime = Calendar.getInstance().getTimeInMillis();
				counter += currentTime - lastCheckedTime;
				lastCheckedTime = currentTime;
				
				if(lastReportedToListeners == 0 || currentTime - lastReportedToListeners >= 1000){
					notifyListeners();
				}
			}
			
		}, delay, delay);
	}
	
	public void pause(){
		newTimer();
	}
	
	public void stop(){
		newTimer();
		counter = 0;
	}
	
	private void newTimer(){
		try{
		timer.cancel();
		timer = new Timer();
		}catch(IllegalStateException e){}
	}
	
	public int getTimeInSeconds(){
		return (int) (counter/1000);
	}
	
	private void notifyListeners(){
		for(Runnable r : listeners){
			r.run();
		}
	}
	
	public void addListener(Runnable r){
		listeners.add(r);
	}
	
	public void removeListener(Runnable r){
		listeners.remove(r);
	}
	
}
