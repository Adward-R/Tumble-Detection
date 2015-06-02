package com.demohn.tumbleapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.util.ArrayList;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Mingchuan on 2015/5/26.
 */
public class TumbleService extends Service{
    private static String TAG = "tumbleService";
    private SensorManager sensorManager;
    private TumbleBinder myBinder;

    private LibSVM libSVM;
    //sensor variables
    private boolean _start_rec;
    private ArrayList<double[]> data_gy = new ArrayList<>();
    private ArrayList<double[]> data_la = new ArrayList<>();
    private ArrayList<double[]> data_gr = new ArrayList<>();
    private ArrayList<double[]> data = new ArrayList<>();

    long _timestamp = 0;

    //broadcaster
    LocalBroadcastManager broadcastManager;

    //intent consts
    static final public String TUMBLE_RESULT = "com.demohn.tumbleapp.TumbleService.dai.hao.shi.ren.yin";
    static final public String TUMBLE_MSG = "com.demohn.tumbleapp.TumbleService.ding.ye.da.tu.hao";

    public void onCreate(){
        _start_rec = false;
        myBinder = new TumbleBinder();
        libSVM = new LibSVM(getApplicationContext());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        SensorRecorder gravityRecorder, linearRecorder, rotateRecorder;
        // init sensors
        gravityRecorder = new SensorRecorder(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        linearRecorder  = new SensorRecorder(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
        rotateRecorder  = new SensorRecorder(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

        //register sensor
        gravityRecorder.register();
        linearRecorder.register();
        rotateRecorder.register();

        //通知栏
        setNotification();

        //UI广播
        broadcastManager = LocalBroadcastManager.getInstance(this);
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }


    //设置通知栏
    public void setNotification(){
        //start notification
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.notify_title))
                .setContentText(getString(R.string.notify_content))
                .setOngoing(true)
                .setAutoCancel(false);

        PendingIntent pendingIntent = createNotificationIntent();
        mBuilder.setContentIntent(pendingIntent);

        //notify
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(2, mBuilder.build());
    }

    private PendingIntent createNotificationIntent(){
        Intent nIntent = new Intent(this, DetectActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,0,nIntent,PendingIntent.FLAG_UPDATE_CURRENT
        );

        return pendingIntent;
    }

    class SensorRecorder implements SensorEventListener {
        private int UPTATE_INTERVAL_TIME = 80;
        Sensor sensor;

        private long lastUpdateTime;

        CallbackImpl impl = new CallbackImpl();


        public SensorRecorder(Sensor sensor) {
            this.sensor = sensor;
        }

        public void register() {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        public void unregister() {
            sensorManager.unregisterListener(this);
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            // 现在检测时间
            long currentUpdateTime = System.currentTimeMillis();
            // 两次检测的时间间隔
            long timeInterval = currentUpdateTime - lastUpdateTime;

            // 判断是否达到了检测时间间隔
            if (timeInterval < UPTATE_INTERVAL_TIME)
                return;
            // 现在的时间变成last时间
            lastUpdateTime = currentUpdateTime;

            //如果满足触发条件，就开始录制
            if(libSVM.triggerRecording(event) && !_start_rec){
                Log.d(TAG, "start REC");
                //清空原来的数据
                data_la.clear();
                data_gr.clear();
                data_gy.clear();
                data.clear();
                _start_rec = true;
                _timestamp = currentUpdateTime;
            }

            if(_start_rec){
                //如果记录时间大于REC_TIME(按照每次获得的时间大约为100ms来计算，录制45*100ms大概可以录下45个点)
                if(currentUpdateTime - _timestamp > libSVM.setRecordingTime()){
                    _start_rec = false;
                    /*int _min = Math.min(Math.min(data_la.size(),Math.min(data_gr.size(),data_gy.size())),30);
                    for(int j=0;j<_min;j++){
                        data.add(data_la.get(j));
                    }
                    for(int j=0;j<_min;j++){
                        data.add(data_gr.get(j));
                    }
                    for(int j=0;j<_min;j++){
                        data.add(data_gy.get(j));
                    }*/
                    for(int j=0;j<data_la.size();j++){
                        data.add(data_la.get(j));
                    }
                    for(int j=0;j<data_gr.size();j++){
                        data.add(data_gr.get(j));
                    }
                    for(int m=0;m<data_gy.size();m++){
                        data.add(data_gy.get(m));
                    }
                    libSVM.judgeSVM(data,impl);
                }else{
                    double[] _data = new double[5];
                    Log.d(TAG,"recording...");
                    _data[0] = sensor.getType(); // sensor type，用int表示
                    _data[1] = currentUpdateTime - _timestamp;
                    _data[2] = event.values[0];
                    _data[3] = event.values[1];
                    _data[4] = event.values[2];

                    if(sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                        data_gr.add(_data);
                        Log.d(TAG, "data_gr:" + _data[0] + " " + _data[1] + " " + _data[2] + " " + _data[3] + " " + _data[4]);
                    }else if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                        data_la.add(_data);
                        Log.d(TAG, "data_la:" + _data[0] + " " + _data[1] + " " + _data[2] + " " + _data[3] + " " + _data[4]);
                    }else if(sensor.getType() == Sensor.TYPE_GRAVITY){
                        data_gy.add(_data);
                        Log.d(TAG,"data_gy:"+_data[0]+" "+_data[1]+" "+_data[2]+" "+_data[3]+" "+_data[4]);
                    }
                }
            }
        }
    }

    /*在判定完了之后*/
    class CallbackImpl implements Callback{

        public void NotifyTaskOnJudgeTure() {
            Intent intent = new Intent(TUMBLE_RESULT);
            intent.putExtra(TUMBLE_MSG,"true");
            broadcastManager.sendBroadcast(intent);
        }

        @Override
        public void NotifyTaskOnJudgeFalse() {
            Intent intent = new Intent(TUMBLE_RESULT);
            intent.putExtra(TUMBLE_MSG,"false");
            broadcastManager.sendBroadcast(intent);
        }

        @Override
        public void NotifyTaskOnProcessing() {
            Intent intent = new Intent(TUMBLE_RESULT);
            intent.putExtra(TUMBLE_MSG,"processing");
            broadcastManager.sendBroadcast(intent);
        }
    }
}

class TumbleBinder extends Binder{
    private static String TAG = "Tumble";
    private int count = 0;
    TumbleBinder(){

    }

    public void startLog(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                count += 1;
                Log.d(TAG,"dingye hao "+count);
            }
        }).start();
    }
}
