package com.example.fallsdetect;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by SHANG on 2017/11/5.
 */

public class SensorOperator {

    private static final String TAG = "SensorOpr";

    protected SensorDataHandler sensorDataHandler; // ??

    private MySensor accSensor;
    private MySensor gyroSensor;

    ArrayList<MySensor> sensorsList = new ArrayList<MySensor>(); // pass this list to SensorDataHandler to let it know all the sensors need to be observe

    //constructor
    SensorOperator(Application application) {
        if(accSensor == null) {
            accSensor = new AccSensor(application);
            sensorsList.add(accSensor);
        }
        if(gyroSensor == null) {
            gyroSensor = new GyroSensor(application);
            sensorsList.add(gyroSensor);
        }
        // instantiate sensor manager
        if (sensorDataHandler == null) {
            sensorDataHandler = new SensorDataHandler(application, sensorsList);
        }
    }

    // do detecting loop if and only if certain sensors are on (doesn't care about other sensors are on if any)
    protected boolean doDetecting() {
        return accSensor.isOn() && gyroSensor.isOn();
    }

    protected sensorData getAccData() {
        return sensorDataHandler.getAccData();
    }

    protected sensorData getGyroData() {
        return sensorDataHandler.getGyroData();
    }

    // this method turn on all the sensors
    protected void turnOnAllSensors() {
        turnOnSensor("Acc");
        turnOnSensor("Gyro");
    }

    // this method turn off all the sensors
    protected void turnOffAllSensors() {
        turnOffSensor("Acc");
        turnOffSensor("Gyro");
    }

    // this method can turn on specific sensor
    public void turnOnSensor(String sensorName)
    {
        switch (sensorName) {
            case "Acc":
                Log.d(TAG, "turnOnAcc");
                // turn on the accelerator
                if(!accSensor.isOn()) {
                    Log.d(TAG, "accSensor.isAccOn() : "+accSensor.isOn() );
                    accSensor.startSensor();
                }
                break;
            case "Gyro":
                Log.d(TAG, "turnOnGyro");
                // turn on the accelerator
                if(!gyroSensor.isOn()) {
                    Log.d(TAG, "gyroSensor.isGyroOn() : "+gyroSensor.isOn() );
                    gyroSensor.startSensor();
                }
                break;
        }
    }

    // this method can turn off specific sensor
    public void turnOffSensor(String sensorName)
    {
        switch (sensorName) {
            case "Acc":
                Log.d(TAG, "turnOffAcc");
                // turn on the accelerator
                if(accSensor.isOn()) {
                    Log.d(TAG, "accSensor.isAccOn() : "+accSensor.isOn() );
                    accSensor.stopSensor();
                }
                break;
            case "Gyro":
                Log.d(TAG, "turnOffGyro");
                // turn on the accelerator
                if(gyroSensor.isOn()) {
                    Log.d(TAG, "gyroSensor.isGyroOn() : "+gyroSensor.isOn() );
                    gyroSensor.stopSensor();
                }
                break;
        }
    }

}
