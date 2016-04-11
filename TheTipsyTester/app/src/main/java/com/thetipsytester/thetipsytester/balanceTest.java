package com.thetipsytester.thetipsytester;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Nick on 4/11/2016.
 */
public class balanceTest extends Activity implements SensorEventListener{


    Sensor accelerometer;
    SensorManager sm;
    TextView acceleration;
    boolean measuring = false;
    float totalAcceleration = 0, changeAcceleration = 0, lastTotal = 0, x, y ,z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_test);

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer= sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView)findViewById(R.id.acceleration);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        changeAcceleration = Math.abs(Math.abs(x) + Math.abs(y) + Math.abs(z) - (float) 10.2);
        if (measuring) {
            totalAcceleration = totalAcceleration + changeAcceleration;
        }
        acceleration.setText("X: " + x +
                            "\nY: " + y +
                            "\nZ: " + z +
                            "\nchange: " + changeAcceleration +
                            "\ntotal: " + totalAcceleration +
                            "\nlast: " + lastTotal);

    }


    public void measure(View view){
        if(measuring){
            lastTotal = totalAcceleration;
            totalAcceleration = 0;
        }
        measuring = !measuring;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
