package com.example.fallsdetect;

import android.util.Log;

public class FallDetectionAlgorithm {
	private static final String TAG = "Algm";

	private static final double accThreshold = 0.4;
	private static final double gyroThreshold = 60.0;
	private static final double lyingThreshold = 35.0;
	private static final double accIntendThreshold = 3.0;
	private static final double gyroIntendThreshold = 200;
	
	private double amin;
	private double amax;
	private double wmin;
	private double wmax;
	private long prevTime;
	private double prevDegr;
	public FallDetectionAlgorithm() {
		amin = 1000;
		amax = 0;
		wmin = 1000;
		wmax = 0;
		prevTime = System.currentTimeMillis();
		prevDegr = 0;
	}
	
	private void calcualteMinMax(double ax, double ay, double az, double wx, double wy, double wz) {
		double acc = Math.sqrt(Math.pow(ax, 2) + Math.pow(ay, 2) + Math.pow(az, 2));
		double gyro = Math.sqrt(Math.pow(wx, 2) + Math.pow(wy, 2) + Math.pow(wz, 2));
		if(acc > amax) {
			amax = acc;
		} else if(acc < amin) {
			amin = acc;
		}
		
		if(gyro > wmax) {
			wmax = gyro;
		} else if(gyro < wmin) {
			wmin = gyro;
		}	
	}
	
	private double calcualatArccos(double ax) {
		return Math.acos(ax/1.0);
	}
	
	private boolean checkStatic() {
		double accDif = Math.abs(amax -amin);
		double wDif = Math.abs(wmax - wmin);
		if(amin!=1000 && wmin!=1000 && accDif < accThreshold && wDif < gyroThreshold) {
			return true;
		} else {
			return false;
		}
	}

    public boolean fallDetection(double[] a, double[] w) {
        return fallDetection(a[0], a[1], a[2], w[0], w[1], w[2]);
    }

	public boolean fallDetection(double ax, double ay, double az, double wx, double wy, double wz) {
		long curTime = System.currentTimeMillis();
		prevDegr = calcualatArccos(ax);
		//Log.i(TAG,"curTime: "+curTime+" prevTime: "+prevTime + " - " + (curTime - prevTime));
		if((curTime - prevTime)/1000 <= 1) {
			//Log.i(TAG,"1?");
			this.calcualteMinMax(ax, ay, az, wx, wy, wz);
		} else {
			//Log.i(TAG,"2?");
			prevTime= curTime;
			amin = 1000;
			amax = 0;
			wmin = 1000;
			wmax = 0;
			prevDegr = calcualatArccos(ax);
		}
		
		if(!checkStatic()) {
			double curDegr = calcualatArccos(ax);
			if(prevDegr < lyingThreshold && curDegr > lyingThreshold) {
				if(amax > accIntendThreshold && wmax > gyroIntendThreshold) {
					return true;
				}
			}
		}
		return false;
	}
	
	
}
