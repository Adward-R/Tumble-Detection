package com.demohn.tumbleapp;

import android.hardware.SensorEvent;
import android.os.Handler;
import libsvm.svm_model;
import libsvm.svm_node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Mingchuan on 2015/5/28.
 */
public class LibSVM implements DetectCondition {

    LibSVM(){

    }
    @Override
    public boolean triggerRecording(SensorEvent event) {
        // 如果Z方向上的线性加速度值小于-5,则返回true,开始录制
        // LAACE指的就是linear acceleration
        return event.sensor.getType() == DetectCondition.LACCE
                && event.values[2] < -5;
    }

    /* 设置录制时间。
    这里的录制时间指的是：当阈值条件被触发时，录制一段时间内的数据以供libSVM分析。
    根据经验，一般传感器事件是每隔100ms触发一次
    返回值是录制时间（以ms为单位）
    * */
    @Override
    public long setRecordingTime() {
        //大概录制40~45个点
        return 100 * 45;
    }

    /* 运用libSVM进行摔倒判定。

    @ArrayList<double[]> sensorData 里每一个item（类型为double数组）的格式：

    item[0] = SENSOR_TYPE (double)(DetectCondition.LACCE | GRAVITY | ROTATE)
    item[1] = RELATIVE_TIME [相对于开始录制时所经过的时间，以ms为单位]
    item[2] = SENSOR_DATA_1 {传感器获得的第一个数据。即 (SensorEvent) event.values[0]。下同。}
    item[3] = SENSOR_DATA_2
    item[4] = SENSOR_DATA_3
    * */
    @Override
    public void judgeSVM(final ArrayList<double[]> sensorData,Callback callback) {
        final int SAMPLING_RATE = 30;
        Handler handler = new Handler();

        final Callback cb = callback;
        //new thread
        new Thread(new Runnable() {
            boolean ifTumble;
            @Override
            public void run() {
                //发送“开始'摔倒判定'的信息到主UI线程上”
                cb.NotifyTaskOnProcessing();

                /*
                TODO :wait for completion
                 */
                //load pre-included train model
                File model_file = new File("tumble.model"); //put inside the app
                BufferedReader model_reader = null;
                svm_model svmModel = null;
                try {
                    model_reader = new BufferedReader(new FileReader(model_file));
                    svmModel = libsvm.svm.svm_load_model(model_reader);
                }
                catch (Exception e){
                    //TODO: alert "Model file not found!"
                }
                if (svmModel==null){
                    //TODO: alert "Model file not found!"
                }
                //build test set
                svm_node[] svm_nodes = new svm_node[SAMPLING_RATE * 9];
                int gravity_cnt = 0, lacce_cnt = 90, rotate_cnt = 180;
                int sensor_cnt = 0;
                while (lacce_cnt < SAMPLING_RATE * 6
                        && gravity_cnt < SAMPLING_RATE
                        && rotate_cnt < SAMPLING_RATE * 9
                        && sensor_cnt < 40){ //not likely to touch this limit
                    if ((int)sensorData.get(sensor_cnt)[0] == DetectCondition.GRAVITY){
                        for (int j=2;j<5;j++){
                            svm_nodes[gravity_cnt] = new svm_node();
                            svm_nodes[gravity_cnt].index = gravity_cnt + 1; //_cnt start from 1, yet index of test data from 0
                            svm_nodes[gravity_cnt].value = sensorData.get(sensor_cnt)[j];
                            gravity_cnt++;
                        }
                        sensor_cnt++;
                    }
                    else if ((int)sensorData.get(sensor_cnt)[0] == DetectCondition.LACCE){
                        for (int j=2;j<5;j++){
                            svm_nodes[lacce_cnt] = new svm_node();
                            svm_nodes[lacce_cnt].index = lacce_cnt + 1;
                            svm_nodes[lacce_cnt].value = sensorData.get(sensor_cnt)[j];
                            lacce_cnt++;
                        }
                        sensor_cnt++;
                    }
                    else if ((int)sensorData.get(sensor_cnt)[0] == DetectCondition.ROTATE){
                        for (int j=2;j<5;j++){
                            svm_nodes[rotate_cnt] = new svm_node();
                            svm_nodes[rotate_cnt].index = rotate_cnt + 1;
                            svm_nodes[rotate_cnt].value = sensorData.get(sensor_cnt)[j];
                            rotate_cnt++;
                        }
                        sensor_cnt++;
                    }
                    else {
                        //TODO: alert "Unknown sensor data type!"
                    }
                }
                //scale
                //predict
                double label = libsvm.svm.svm_predict(svmModel, svm_nodes);
                if ((int)label==1){
                    ifTumble = true;
                }
                else {
                    ifTumble = false;
                }

                // 如果判定为摔倒
                if(ifTumble) {
                    //发送“'真摔倒了'的信息到主UI线程上”
                    cb.NotifyTaskOnJudgeTure();
                }
                else{
                    //发送“'这货是误判'的信息到主UI线程上”
                    cb.NotifyTaskOnJudgeFalse();
                }
            }
        }).start();
    }
}