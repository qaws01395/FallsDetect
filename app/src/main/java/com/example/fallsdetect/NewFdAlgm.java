package com.example.fallsdetect;
import android.util.Log;
/**
 * Created by SHANG on 2017/11/24.
 */

public class NewFdAlgm {
    private static final String TAG = "Algm";

    private double [][]accdata;
    private double [][]gyrodata;


    private static final int datalenOri = 40;
    private static final int datalen = 10;
    private static final int datawidth = 3;

    private static final double accThreshold = 10; //0.4G
    private static final double angvelThreshold = 180;//angle velocity, this one too sensitive
    private static final double angleThreshold = 65.0;//angle change, need more test!, looks OK

    //private static final double lyingThreshold = 35.0;

    private static final double VDAaccThreshold = 30;//3.0G, Threshold for vigorous daily activities such as jumping, running, going upstairs/downstairs quickly
    private static final double VDAgyroThreshold = 550;// Threshold for vigorous daily activities such as jumping, running, going upstairs/downstairs quickly
    private static final double VDAdegree = 135;

    private double amin;
    private double amax;
    private double wmin;
    private double wmax;

    double degree;

    NewFdAlgm(float [][]accrcv, float [][]gyrorcv){
        mvfilter(accrcv,gyrorcv);
    }

    private void mvfilter(float [][]accrcv, float [][]gyrorcv){ //mean-value filter
        accdata = new double[datalen][datawidth];
        gyrodata = new double[datalen][datawidth];

        double []tmpa = new double[datawidth];
        double []tmpw = new double[datawidth];

        for(int i=0,counttmp=0,count=0; i<datalenOri; i++){
            //Log.i(TAG, "count: " + i + " ax: " + accrcv[i][0] + " ay: " + accrcv[i][1] + " az: " + accrcv[i][2] );
            //Log.i(TAG, "hudu count: " + i + " wx: " + gyrorcv[i][0] + " wy: " + gyrorcv[i][1] + " wz: " + gyrorcv[i][2] );
            //Log.i(TAG,"jiaodu count: " + i + " wx: " + Math.toDegrees(gyrorcv[i][0]) + " wy: " + Math.toDegrees(gyrorcv[i][1]) + " wz: " + Math.toDegrees(gyrorcv[i][2]));

            for (int j=0; j<datawidth; j++) {
                tmpa[j] += Double.parseDouble(new Float(accrcv[i][j]).toString()); // second fast
                tmpw[j] += Math.toDegrees(gyrorcv[i][j]);
            }
            counttmp++;
            if(counttmp == 4){  //caculate average of 4 data at a time
                counttmp = 0;
                for (int j=0; j<datawidth; j++) {
                    accdata[count][j] = tmpa[j]/4.0;
                    gyrodata[count][j] = tmpw[j]/4.0;
                    tmpa[j] = 0;
                    tmpw[j] = 0;
                }
                //Log.i(TAG, "count: " + count + " ax: " + accdata[count][0] + " ay: " + accdata[count][1] + " az: " + accdata[count][2] );
                //Log.i(TAG, "count: " + count + " wx: " + gyrodata[count][0] + " wy: " + gyrodata[count][1] + " wz: " + gyrodata[count][2] );
                //Log.i(TAG,"------------------------------------------------------------------------------------");
                count++;
            }
        }
    }

    public void drawPic(){
        for(int i=0; i<datalen; i++) {
            //Log.i(TAG, "count: " + i + " ax: " + accdata[i][0] + " ay: " + accdata[i][1] + " az: " + accdata[i][2] );
            //Log.i(TAG, "count: " + i + " wx: " + gyrodata[i][0] + " wy: " + gyrodata[i][1] + " wz: " + gyrodata[i][2] );

            double ax = accdata[i][0];
            double ay = accdata[i][1];
            double az = accdata[i][2];

            double wx = gyrodata[i][0];
            double wy = gyrodata[i][1];
            double wz = gyrodata[i][2];

            double acc = Math.sqrt(Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2));
            double gyro = Math.sqrt(Math.pow(wx, 2) + Math.pow(wy, 2) + Math.pow(wz, 2));
            Log.i(TAG, "acc: " + acc + " gyro: " + gyro );
        }

        final double dT = 0.1;

        double wx = 0;
        double wy = 0;
        double wz = 0;

        for(int i=0; i<datalen; i++) {  //use integral to caculate angle change
            wx += gyrodata[i][0] * dT;
            wy += gyrodata[i][1] * dT;
            wz += gyrodata[i][2] * dT;
            //Log.i(TAG,"wx " + wx +"wy " + wy +"wz " + wz);
        }
        double degree = Math.sqrt(Math.pow(wx, 2) + Math.pow(wy, 2) + Math.pow(wz, 2));
        Log.i(TAG,"degree: " + degree);

    }

    private void getMinMax(){
        amin = 1000;
        amax = 0;
        wmin = 1000;
        wmax = 0;
        for(int i=0; i<datalen; i++){
            //Log.i(TAG, "count: " + i + " ax: " + accdata[i][0] + " ay: " + accdata[i][1] + " az: " + accdata[i][2] );
            //Log.i(TAG, "count: " + i + " wx: " + gyrodata[i][0] + " wy: " + gyrodata[i][1] + " wz: " + gyrodata[i][2] );

            double ax = accdata[i][0];
            double ay = accdata[i][1];
            double az = accdata[i][2];

            double wx = gyrodata[i][0];
            double wy = gyrodata[i][1];
            double wz = gyrodata[i][2];

            double acc = Math.sqrt(Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2));
            double gyro = Math.sqrt(Math.pow(wx, 2) + Math.pow(wy, 2) + Math.pow(wz, 2));

            //Log.i(TAG,"acc " + acc + " gyro " + gyro);

            if(acc > amax) {amax = acc;}
            if(acc < amin) {amin = acc;}

            if(gyro > wmax) {wmax = gyro;}
            if(gyro < wmin) {wmin = gyro;}

            //Log.i(TAG,"amax " + amax + " amin " + amin +" wmax " + wmax +" wmin " + wmin );
            //Log.i(TAG,"------------------------------------------------------------------------------------");

        }
    }

    private boolean phase1(int flag){ //vigorous movement
        double thisAccThreshold;
        double thisAngvelThreshold;
        if(flag == 0){
            thisAccThreshold = accThreshold;
            thisAngvelThreshold = angvelThreshold;
        }
        else{//judge if moving
            thisAccThreshold = accThreshold/5;//need more test
            thisAngvelThreshold = angvelThreshold/5;//need more test
        }

        getMinMax();
        double accDif = Math.abs(amax -amin);
        double wDif = Math.abs(wmax - wmin);
        Log.i(TAG,"amax " + amax + " amin " + amin);
        Log.i(TAG,"wmax " + wmax +" wmin " + wmin );
        Log.i(TAG,"----------------------------------------------");
        Log.i(TAG,"accDif: " + accDif + " wDif: " + wDif);
        if(accDif < thisAccThreshold && wDif < thisAngvelThreshold) { // I think '&&' is better
            return false;
        } else {
            return true;
        }
    }

    private boolean phase2(){ //angle change
        final double dT = 0.1;

        double wx = 0;
        double wy = 0;
        double wz = 0;

        degree = 0;

        for(int i=0; i<datalen; i++) {  //use integral to caculate angle change
            wx += gyrodata[i][0] * dT;
            wy += gyrodata[i][1] * dT;
            wz += gyrodata[i][2] * dT;
            //Log.i(TAG,"wx " + wx +"wy " + wy +"wz " + wz);
        }
        degree = Math.sqrt(Math.pow(wx, 2) + Math.pow(wy, 2) + Math.pow(wz, 2));
        Log.i(TAG,"degree " + degree);
        Log.i(TAG,"----------------------------------------------");
        if(degree > angleThreshold){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean phase3(){
        if(amax > VDAaccThreshold || wmax > VDAgyroThreshold || degree > VDAdegree){ //if doing vigorous daily activities then ignore
            return false;
        }
        else{
            return true;
        }
    }


    protected boolean mayfall(){
        if(phase1(0)){
            Log.i(TAG,"phase 1: true");
            if(phase2()){
                Log.i(TAG,"phase 2: true");
                if(phase3()){
                    Log.i(TAG,"phase 3: true");
                    return true;
                }
            }
        }
        return false;
    }
    protected boolean considerfall(){//after mayfall() if people don't move for 3s, then consider already fall
        if(phase1(1)){ //movement
            Log.i(TAG,"considerfall: false");
            return false;
        }
        else{   //no movement, already fall
            return true;
        }
    }
    protected boolean algm(int flag){
        if(flag == 0){
            return mayfall();
        }
        else{
            return considerfall();
        }
    }
}
