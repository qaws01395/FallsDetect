package com.example.fallsdetect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hschang on 2017/11/16.
 */

public abstract class MySensor {

    protected List<SensorDataHandler> observers = new ArrayList<SensorDataHandler>();// usage of observer class


    protected abstract boolean isOn();
    protected abstract void notifySensorDataHandler();
    protected abstract void startSensor();
    protected abstract void stopSensor();

    // provide a method to let observer register this subject
    public void register(SensorDataHandler observer) {
        observers.add(observer);
    }

    // provide a method to let observer unregister this subject
    public void unregister(SensorDataHandler observer) {
        observers.remove(observer);
    }
}
