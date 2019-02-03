package com.example.fallsdetect;

import android.util.Log;

/**
 * Created by SHANG on 2017/11/24.
 */

public class sensorData {
    private static final String TAG = "AccData";

    private static final int datalenOri = 40;

    private float [][] data;
    private int count;
    private boolean ready;
    sensorData(){
        ready = false;
        count = 0;
        data = new float [datalenOri][3];
    }
    protected void insert(float [] rcvdata){
        int n = rcvdata.length;
        for (int i=0; i<n; i++) {
            data[count][i] = rcvdata[i];
        }
        if(count == datalenOri-1){
            ready = true;
        }
        //Log.i(TAG, "count: " + count + " ax: " + data[count][0] + " ay: " + data[count][1] + " az: " + data[count][2] );
        count = (count + 1)%datalenOri;

        /*
        double acc = Math.sqrt(Math.pow(data[0], 2) + Math.pow(data[1], 2) + Math.pow(data[2], 2));
        Log.i(TAG, " acc: " + acc  + " amax: " + amax + " amin: " + amin);
        if(acc > amax) {
            amax = acc;
        } else if(acc < amin) {
            amin = acc;
        }
        Log.i(TAG, " ax: " + data[0] + " ay: " + data[1] + " az: " + data[2] + " amax: " + amax + " amin: " + amin);*/
    }
    protected int getCount(){
        return count;
    }
    protected boolean isReady(){
        return ready;
    }
    protected float [][]getData(){
        return data;
    }
}
