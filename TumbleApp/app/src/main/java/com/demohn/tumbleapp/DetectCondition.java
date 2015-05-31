package com.demohn.tumbleapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.ArrayList;

/**
 * Created by Mingchuan on 2015/5/28.
 */
public interface DetectCondition {
    int GRAVITY = Sensor.TYPE_GRAVITY;
    int LACCE   = Sensor.TYPE_LINEAR_ACCELERATION;
    int ROTATE  = Sensor.TYPE_GYROSCOPE;

    boolean triggerRecording(SensorEvent event);
    long setRecordingTime();
    void judgeSVM(ArrayList<double[]> data,Callback callback);
}
