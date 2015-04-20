
package com.example.acceleration;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.BatchUpdateException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.WeakHashMap;

public class MainActivity extends Activity {

    TextView t1, t2, t3;
    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 80;

    // 传感器管理器
    private SensorManager sensorManager;

    // 传感器
    SensorRecorder gravitySensor, linearAccelerationSensor,rotationSensor;

    FileStorage fsGravity;
    FileStorage fsLinear;
    FileStorage fsRotate;
    //开始时间
    long startTimestamp;

    // 场景名称
    String spinnerItem;
    //textview
    TextView logGravity, logRotate, logAcce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        final Button btn_start = (Button) findViewById(R.id.start_record);
        final Button btn_stop  = (Button) findViewById(R.id.stop_record);
        Button btn_back = (Button) findViewById(R.id.back);

        logGravity = (TextView) findViewById(R.id.log_gravity);
        logAcce = (TextView) findViewById(R.id.log_acce);
        logRotate = (TextView) findViewById(R.id.log_rotate);
        btn_start.setEnabled(false);
        btn_stop.setEnabled(false);
        // add listeners
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimestamp = System.currentTimeMillis();

                fsGravity = new FileStorage(MainActivity.this, "GRAVITY", startTimestamp);
                fsLinear = new FileStorage(MainActivity.this, "LACCE", startTimestamp);
                fsRotate = new FileStorage(MainActivity.this, "ROTATE", startTimestamp);

                gravitySensor = new SensorRecorder(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), fsGravity);
                linearAccelerationSensor = new SensorRecorder(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), fsLinear);
                rotationSensor = new SensorRecorder(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), fsRotate);

                registerAll();

                fsGravity.writeHeader(UPTATE_INTERVAL_TIME, spinnerItem, "+TIME Gx Gy Gz");
                fsLinear.writeHeader(UPTATE_INTERVAL_TIME, spinnerItem, "+TIME aX aY aZ");
                fsRotate.writeHeader(UPTATE_INTERVAL_TIME, spinnerItem, "+TIME rX rY rZ");
                Toast t = Toast.makeText(MainActivity.this, "start recording...", Toast.LENGTH_SHORT);
                t.show();

                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
            }
        });

        //"停止录制"按钮的实现过程
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unregisterAll();
                Toast t = Toast.makeText(MainActivity.this,"stop recording...",Toast.LENGTH_SHORT);
                t.show();
                btn_stop.setEnabled(false);
                btn_start.setEnabled(true);
            }
        });

        //“退出程序”按钮的实现
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        //spinner 场景选项
        Spinner spinner = (Spinner) findViewById(R.id.scene_spinner);
        final TestScenes ts = new TestScenes();
        ArrayList<String> list = ts.getArray();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    spinnerItem = ts.getItem(position);
                    btn_start.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    protected void onStop(){
        super.onStop();
    }

    public void registerAll(){
        gravitySensor.register();
        linearAccelerationSensor.register();
        rotationSensor.register();
    }

    public void unregisterAll(){
        gravitySensor.unregister();
        linearAccelerationSensor.unregister();
        rotationSensor.unregister();
    }
    class SensorRecorder implements SensorEventListener {
        Sensor sensor;
        FileStorage fs;
        private long lastUpdateTime;

        public SensorRecorder(Sensor sensor, FileStorage fs) {
            this.sensor = sensor;
            this.fs = fs;
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

            if(fs.getSensorType().equals("GRAVITY")
                    || fs.getSensorType().equals("LACCE")
                    || fs.getSensorType().equals("ROTATE")
                    ){

                //保留小数点后3位，四舍五入
                DecimalFormat df = new DecimalFormat("0.000");
                df.setRoundingMode(RoundingMode.HALF_UP);

                long deltaTime = lastUpdateTime - startTimestamp;
                String value = "+"+deltaTime+" "+df.format(event.values[0])+" "+df.format(event.values[1])+" "+df.format(event.values[2]);
                try{
                    fs.appendValue(value);

                    if(fs.getSensorType().equals("GRAVITY")){
                        logGravity.setText(value);
                    }

                    if(fs.getSensorType().equals("LACCE")){
                        logAcce.setText(value);
                    }

                    if(fs.getSensorType().equals("ROTATE")){
                        logRotate.setText(value);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class FileStorage {
        Context context;
        String sensorType = "";
        long timestamp = 0;
        File file;
        FileWriter fw;
        FileStorage(Context context, String sensorType, long timestamp){
            this.context = context;
            this.sensorType = sensorType;
            this.timestamp = timestamp;


            if(isSDcardStorageWritable()){
                File file_dir = new File(Environment.getExternalStorageDirectory(),"hn_sensor");
                file_dir.mkdirs();
                file = new File(file_dir,sensorType+"-"+timestamp+".data");
            }else{
                file = new File(context.getFilesDir(),sensorType+"-"+timestamp+".data");
            }
        }

        public boolean isSDcardStorageWritable(){
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }
        public String getSensorType(){
            return sensorType;
        }
        /* data File structure:
        name: GRAVITY-123456789.data
        START_TIME=123456789
        INTERVAL=100
        SENSOR= GRAVITY | LINEAR_ACCELERATOR | ROTATION
        NOTE= **关于这个数据文件的备注**
        COLS=+TIME X Y Z
        ==============
        +0 0.000 0.000 0.000
        +0.000 0.000 0.000
        ...
        * */
        public void writeHeader(long Interval,String note,String cols){
            try{
                fw = new FileWriter(file,true);
                BufferedWriter fbw = new BufferedWriter(fw);

                String header_model = "START_TIME="+timestamp+"\n"+
                        "INTERVAL="+Interval+"\n"+
                        "SENSOR="+sensorType+"\n"+
                        "SENCE="+note+"\n"+
                        "COLS="+cols+"\n"+
                        "===================="+"\n"; // 一定是20个=

                fbw.write(header_model);
                fbw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void appendValue(String value) throws IOException {
            fw = new FileWriter(file,true);
            BufferedWriter fbw = new BufferedWriter(fw);

            fbw.write(value);
            fbw.newLine();
            fbw.close();
        }
    }
}
