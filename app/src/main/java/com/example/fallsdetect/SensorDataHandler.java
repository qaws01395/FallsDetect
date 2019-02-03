package com.example.fallsdetect;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by SHANG on 2017/11/5.
 *
 * This class basically collects the data from all the sensors .
 * The implementation of this class follows the observer of the observer pattern.
 */

public class SensorDataHandler {
    private static final String TAG = "SensorDataHandler";
    //context
    private Application application;

    private ArrayList<MySensor> subject = new ArrayList<MySensor>();// subject that the observer observe
    // collect data
    //private double[] sensorData = new double[3];
    private sensorData accdata;
    private sensorData gyrodata;

    //constructor
    SensorDataHandler(Application application, ArrayList<MySensor> sensorArrayList) {
        this.application = application;

        int n = sensorArrayList.size();
        for (int i=0; i<n; i++) {
            this.subject.add(sensorArrayList.get(i));
            this.subject.get(i).register(this);
        }
    }

    /*protected double[] getAccData() {
        return sensorData;
    }*/
    protected sensorData getAccData() {
        return accdata;
    }

    protected sensorData getGyroData() {
        return gyrodata;
    }

    public void update(String sensorName, sensorData sensordata) {
        //Log.d(TAG,"sensor: "+sensorName+" x: "+data[0]+" y: "+data[1]+" z: "+data[2] );

        //int n = data.length;

        switch (sensorName) {
            case "AccSensor":
                accdata = sensordata;
                /*for (int i=0; i<n; i++) {
                    // convert float to double precisely and quickest
                    // credit; http://programmingjungle.blogspot.com/2013/03/float-to-double-conversion-in-java.html
//                    sensorData[i] = new FloatingDecimal(data[i]).doubleValue(); // somehow the class cannot be found =__= (not sure why... openjdk?)
                                                                                // might need to try installing this http://openjdk.java.net/
                    sensorData[i] = Double.parseDouble(new Float(data[i]).toString()); // second fast
                }*/

                break;
            case "GyroSensor":
                gyrodata = sensordata;
                break;
        }
    }

}
