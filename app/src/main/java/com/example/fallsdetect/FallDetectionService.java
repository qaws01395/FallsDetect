package com.example.fallsdetect;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by hschang on 2017/11/1.
 *
 *  This class is implement as a service that
 *  Note: To be able to call those functions, a class needs to bind this service first.
 */

public class FallDetectionService extends Service {
    private static final String TAG = "FallDetectionService";

    private static final int datalenOri = 40;
    private static final int datawidth = 3;

    // utilities
    private NewFdAlgm fallDetectionAlgorithm;

    /* true means a child thread is running and we set true to kill it
       false means a child thread is running */
    private volatile boolean stop_running = true; // used to stop the while loop in the new thread
    private int detectingAlgmDelay = 1000;

    // notification
    private final int serviceOnNotifyID = 1; // 通知的識別號碼
    private final int requestCode = serviceOnNotifyID; // PendingIntent的Request Code
    private NotificationManager notificationManager;

    // can be used for other classes if bind this service
    protected SensorOperator sensorOperator;

    // argument of settings
    private int timeout;
    private  String message;
    private  String user;

    @Override
    // start fall detecting once this service is on
    public void onCreate() // executes when the service first created
    {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        Log.d(TAG, "new Service thread ID is "+ Thread.currentThread().getId());

//        notifySystem = new Notifition(getApplication(), getBaseContext());

        sensorOperator = new SensorOperator(getApplication());
        sensorOperator.turnOnAllSensors();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) // executes when the service is created and startService() was called
    {
        Log.i(TAG, "onStartCommand()");
        startdetect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return mLocBin;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.i(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    // stop fall detecting only when this service is destroyed
    public void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        // kill the child thread
        if(!stop_running) stop_running = true;
        //turn off sensors
        sensorOperator.turnOffAllSensors();
        // clean up other stuff
        if (sensorOperator != null) sensorOperator = null;
        if (fallDetectionAlgorithm != null) fallDetectionAlgorithm = null;

        cancelNotification();
    }

    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        FallDetectionService getService()
        {
            return  FallDetectionService.this;
        }
    }

    private LocalBinder mLocBin = new LocalBinder();

    // following are methods that can called by and other classes if they bind this service

    // from service to dialog
    protected void showDialog(String user, int timeout, String message){
        Intent startIntent = new Intent(getBaseContext(), DialogCompatActivity.class);
//        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // cannot go back to the previous activity with the BACK button
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.putExtra("usr", user);
        startIntent.putExtra("timeout", timeout*1000);
        startIntent.putExtra("msg", message);
        startActivity(startIntent);
    }

    // create the notification bar on the top
    private void createNotification() {
        // ONE_SHOT：PendingIntent只使用一次；
        // CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；
        // NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；
        // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        final int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        final PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), requestCode, new Intent(getBaseContext(), MainActivity.class), flags); // 取得PendingIntent

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());//使用兼容版本
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置状态栏的通知图标
        builder.setAutoCancel(false);//禁止用户点击删除按钮删除
        builder.setOngoing(true); //禁止滑动删除
        builder.setShowWhen(true);//右上角的时间显示
        builder.setContentTitle("Notification: Start detecting fall");//设置通知栏的标题内容
        builder.setContentText("Fall Detection mode On");
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();//创建通知
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        notificationManager.notify(serviceOnNotifyID, notification);
//        startForeground(1,notification); //设置为前台服务
    }

    // delete the notification bar on the top
    private void cancelNotification() {
        notificationManager.cancel(serviceOnNotifyID);// clear the notification
    }

    //For sending out messgaes. Notifition
//    protected void sendOutNotification() {
//        Intent startIntent = new Intent(getBaseContext(), DialogCompatActivity.class);
////        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // cannot go back to the previous activity with the BACK button
//        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startIntent);
//    }

    protected void updateArgument(String name, Object object) {
        switch (name) {
            case "Timeout":
                timeout = (int) object;
                break;
            case "Message":
                message = (String) object;
                break;
            case "UserName":
                user = (String) object;
                break;
        }
    }

    private float [][][] getDataforalgm(sensorData accdata, sensorData gyrodata){
        float [][][] ret = new float [2][datalenOri][datawidth];
        float [][] accrcv = accdata.getData();
        float [][] accsnt = new float [datalenOri][datawidth];
        int pa = accdata.getCount();

        float [][] gyrorcv = gyrodata.getData();
        float [][] gyrosnt = new float [datalenOri][datawidth];
        int pg = gyrodata.getCount();

        for(int i=0; i<datalenOri; i++){
            accsnt[i] = accrcv[pa];
            gyrosnt[i] = gyrorcv[pg];
            pa = (pa+1)%datalenOri;
            pg = (pg+1)%datalenOri;
        }
        ret[0] = accsnt;
        ret[1] = gyrosnt;
        return ret;
    }

    protected void startdetect(){
        //turn on sensors
        sensorOperator.turnOnAllSensors();

        if(stop_running) {

            stop_running = false;

            new Thread(new Runnable() {
                private sensorData accdata;
                private sensorData gyrodata;
                @Override
                public void run() {
                    // 开始执行后台任务
                    // Log.i(TAG, "new Thread thread ID is "+ Thread.currentThread().getId());
                    while(!stop_running) {
                        try {
                            Thread.sleep(detectingAlgmDelay);// sleep
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Log.i(TAG, "stop_running "+ stop_running);
//                        // update data
                        if(sensorOperator == null) {
                            Log.i(TAG,"break?");
                            break;
                        }
                        //Log.i(TAG,"Should be something here");
                        accdata = sensorOperator.getAccData();
                        gyrodata = sensorOperator.getGyroData();
                        if(accdata== null || gyrodata== null || accdata.isReady() == false || gyrodata.isReady() == false) {
                            Log.i(TAG,"continue?");
                            continue;
                        }
                        float [][][] snt = getDataforalgm(accdata,gyrodata);

                        fallDetectionAlgorithm = new NewFdAlgm(snt[0],snt[1]);
                        //fallDetectionAlgorithm.drawPic();
                        if(fallDetectionAlgorithm.algm(0)){//phase 123 to decide if fall happen
                            //if(true){
                            new Thread(new Runnable() { //new thread to listen 3s to decide if already fall
                                private sensorData accdata;
                                private sensorData gyrodata;
                                private boolean fallFlag = false;
                                @Override
                                public void run() {
                                    for(int k=0;k<2;k++) {//thread runs 3s
                                        try {
                                            Thread.sleep(detectingAlgmDelay);// sleep
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if(sensorOperator == null) {
                                            Log.i(TAG,"break?");
                                            break;
                                        }
                                        //Log.i(TAG,"Should be something here");
                                        accdata = sensorOperator.getAccData();
                                        gyrodata = sensorOperator.getGyroData();
                                        if(accdata== null || gyrodata== null || accdata.isReady() == false || gyrodata.isReady() == false) {
                                            Log.i(TAG,"continue?");
                                            continue;
                                        }
                                        float [][][] snt = getDataforalgm(accdata,gyrodata);
                                        fallDetectionAlgorithm = new NewFdAlgm(snt[0],snt[1]);
                                        if(fallDetectionAlgorithm.algm(1)){
                                            fallFlag = true;
                                        }
                                        Log.i(TAG,"2nd thread ===========================================================================================");
                                    }
                                    if(fallFlag == true){
                                        sensorOperator.turnOffAllSensors();
                                        stop_running = true;
                                        showDialog(user, timeout, message);
                                    }
                                    Log.i(TAG,"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ fallFlag " + fallFlag);
                                }
                            }).start();
                        }


                        // double [] tmp;
                        //tmp = sensorData.getData();
                        //Log.i(TAG, "isready " + sensorData.isReady());
                        // Log.i(TAG,"amax: " + sensorData.getMax() + "amin: " + sensorData.getMin());
                        //Log.i(TAG, "Gyro: x: "+ gyroData[0] + "y: " + gyroData[1]+ "z: "  + gyroData[2]);
                        // detecting fall here
                        //if (fallDetectionAlgorithm.fallDetection(sensorData, gyroData) ) {
                        //sensorOperator.turnOffAllSensors();
                        //Log.i(TAG, "wyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
                        //showDialog();
                        //stop_running = true;
                        //break;
                        //}
                        //Log.i(TAG,"1st thread ===========================================================================================");
                    }
                    Log.i(TAG, "Service thread ID with "+ Thread.currentThread().getId()+" is ended");
                }
            }).start();

            createNotification();
        }
    }

}
