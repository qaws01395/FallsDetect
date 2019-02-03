package com.example.fallsdetect;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

//import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;

/**
 * Created by SHANG on 2017/10/30.
 *
 * This class is implemented as a accelerator.
 * Basically turn on/off the acc sensor service provided by Android.
 */

public class AccSensor extends MySensor{

    private static final String TAG = "AccSensor";

    private SensorManager sensorManager;
    private boolean isRegister = false;

    //private float X_lateral, Y_longitudinal, Z_vertical;
    private float [] data;

    private sensorData accdata;

    // initiate sensor manager
    AccSensor(Application application) {
        //创建一个SensorManager来获取系统的传感器服务
        this.sensorManager = (SensorManager)application.getSystemService(Context.SENSOR_SERVICE);
    }

    private sensorData getData() {
        return accdata;
    }

    @Override
    protected void startSensor(){

        accdata = new sensorData();
        data = new float[3];

        //选取加速度感应器
        int sensorType = Sensor.TYPE_ACCELEROMETER;
        //int sensorType = TYPE_LINEAR_ACCELERATION;
        /*
             * 最常用的一个方法 注册事件
             * 参数1 ：SensorEventListener监听器
             * 参数2 ：MySensor 一个服务可能有多个Sensor实现，此处调用getDefaultSensor获取默认的Sensor
             * 参数3 ：模式 可选数据变化的刷新频率
             * */
        Log.d(TAG,"accelerator registered.");
        isRegister = sensorManager.registerListener(myAccelerometerListener, sensorManager.getDefaultSensor(sensorType), 25000);
    }

    // listener action
    final SensorEventListener myAccelerometerListener = new SensorEventListener() {
        //复写onSensorChanged方法
        public void onSensorChanged(SensorEvent sensorEvent){

            if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //if(sensorEvent.sensor.getType() == TYPE_LINEAR_ACCELERATION){

                data[0] = sensorEvent.values[0];
                data[1] = sensorEvent.values[1];
                data[2] = sensorEvent.values[2];

                accdata.insert(data);

                // detecting if fall occur using accelerator
//                if(delta_x + delta_y + delta_z >= 25) {
//                    stopSensor();
//                    Log.d(TAG, "What is context here? "+ context);
//                    showDialog();
//                }

//                Log.i(TAG,"MySensor Thread ID: "+ Thread.currentThread().getId());
               //Log.i(TAG,"x " + data[0] +" y "+data[1] + " z: "+data[2] );

                notifySensorDataHandler();
            }
        }

        //复写onAccuracyChanged方法
        public void onAccuracyChanged(Sensor sensor , int accuracy){
//            Log.i(TAG, "onAccuracyChanged");
        }

    };// end SensorEventListener()
    /*    public void onPause(){
            *//*
         * 很关键的部分：注意，说明文档中提到，即使activity不可见的时候，感应器依然会继续的工作，测试的时候可以发现，没有正常的刷新频率
         * 也会非常高，所以一定要在onPause方法中关闭触发器，否则讲耗费用户大量电量，很不负责。

         * *//*
        sm.unregisterListener(myAccelerometerListener);
        super.onPause();
    }*/
    @Override
    protected void stopSensor() {
        if(isRegister) {
            Log.d(TAG,"accelerator unregistered.");
            sensorManager.unregisterListener(myAccelerometerListener);
            isRegister = false;
        }
    }

    @Override
    protected boolean isOn() {
        return isRegister;
    }

    @Override
    // a method to notify all the registered observers
    protected void notifySensorDataHandler() {
        for (SensorDataHandler observer : observers) {
            observer.update("AccSensor", getData());// all the observer do this
        }
    }

}
