
package com.example.acceleration;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView t1, t2, t3;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 100;

    // 传感器管理器
    private SensorManager sensorManager;
    // 传感器
    private Sensor accelerometerSensor, gravitySensor, linearAccelerationSensor;
    SensorDemo SensorDemo1, SensorDemo2, SensorDemo3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        SensorDemo1 = new SensorDemo(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), t1);
        SensorDemo2 = new SensorDemo(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), t2);
        SensorDemo3 = new SensorDemo(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), t3);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        SensorDemo1.register();
        SensorDemo2.register();
        SensorDemo3.register();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        SensorDemo1.unregister();
        SensorDemo2.unregister();
        SensorDemo3.unregister();
    }

    class SensorDemo implements SensorEventListener {

        TextView t;
        Sensor sensor;

        private long lastUpdateTime;

        public SensorDemo(Sensor sensor, TextView t) {

            this.t = t;
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
        public void onSensorChanged(SensorEvent arg0) {
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
            t.setText("x:" + arg0.values[0] + "\ny:" + arg0.values[1] + "\nz:" + arg0.values[2]);

        }

    }

}
