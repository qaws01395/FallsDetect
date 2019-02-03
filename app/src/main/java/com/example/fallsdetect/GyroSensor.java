package com.example.fallsdetect;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by SHANG on 2017/11/5.
 *
 * This class is implemented as a
 * Basically turn on/off the gyro sensor service provided by Android.
 */

public class GyroSensor extends MySensor {
    private static final String TAG = "GyroSensor";
    //context
    private Application application;
    //sensor
    private SensorManager sensorManager;
    private boolean isRegister = false;
    private int sensorType;
    //sensor data
    //private static final float NS2S = 1.0f / 1000000000.0f;
    //private float timestamp;
    private float angle[] = new float[3];
    private sensorData gyrodata;
    //construction
    GyroSensor(Application application) {
        gyrodata = new sensorData();
        this.sensorManager = (SensorManager)application.getSystemService(Context.SENSOR_SERVICE);
        this.sensorType = Sensor.TYPE_GYROSCOPE;
        this.application = application;
    }

    @Override
    //register sensor
    protected void startSensor() {
        Log.d(TAG,"gyroscope registered.");
        isRegister = sensorManager.registerListener(myGyroscopeListener, sensorManager.getDefaultSensor(sensorType), 25000);
    }

    // listener action
    final SensorEventListener myGyroscopeListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent){
            if(sensorEvent.sensor.getType() == sensorType){
                //final float dT = (sensorEvent.timestamp -timestamp) * NS2S;
               // Log.i(TAG,"dT" + dT);
                angle[0] = sensorEvent.values[0];
                angle[1] = sensorEvent.values[1];
                angle[2] = sensorEvent.values[2];
                gyrodata.insert(angle);

                notifySensorDataHandler();
                //将当前时间赋值给timestamp
            }
        }

        //复写onAccuracyChanged方法
        public void onAccuracyChanged(Sensor sensor , int accuracy){
//            Log.i(TAG, "onAccuracyChanged");
        }

    };// end SensorEventListener()

/*    public void onPause(){
        super.onPause();
    }*/

    @Override
    protected void stopSensor(){
        if(isRegister) {
            Log.d(TAG,"gyroscope unregistered.");
            sensorManager.unregisterListener(myGyroscopeListener);
            isRegister = false;
        }
    }

    @Override
    protected boolean isOn() {
        return isRegister;
    }

    private sensorData getData() {
        //Log.i(TAG, "Gyro: x: "+ angle[0] + "y: " + angle[1]+ "z: "  + angle[2]);
        return gyrodata;
    }

    @Override
    protected void notifySensorDataHandler() {
        for (SensorDataHandler observer : observers) {
            observer.update("GyroSensor",  getData());// all the observer do this
        }
    }

}
