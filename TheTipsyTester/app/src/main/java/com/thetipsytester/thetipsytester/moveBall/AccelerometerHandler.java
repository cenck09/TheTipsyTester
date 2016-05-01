package com.thetipsytester.thetipsytester.moveBall;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerHandler implements SensorEventListener {
	
	private float axisX;
    private float axisY;
    private float axisZ;

	public AccelerometerHandler(Activity activity) {
		SensorManager sensorManager;
		sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public synchronized void onSensorChanged(SensorEvent event) {
		axisX = event.values[0];
	    axisY = event.values[1];
	    axisZ = event.values[2];
	}

	public synchronized float getAxisX() {
		return axisX;
	}

	public synchronized float getAxisY() {
		return axisY;
	}

	public synchronized float getAxisZ() {
		return axisZ;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	

}
