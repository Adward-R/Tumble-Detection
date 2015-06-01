package com.demohn.tumbleapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DetectActivity extends ActionBarActivity implements View.OnLongClickListener{

    //receive broadcasts from service
    BroadcastReceiver broadcastReceiver;
    TextView status;
    String PhoneNumber;
    LinearLayout CancelLayout;
    //vibrator
    Vibrator vibrator;
    Handler handler;
    Runnable vibrateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        status = (TextView) findViewById(R.id.status);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        CancelLayout = (LinearLayout) findViewById(R.id.cancel_layout);
        //启动服务
        startService();

        //监听service
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(TumbleService.TUMBLE_MSG);
                handleTumbleResult(s);
            }
        };

        //get phone number
        Intent intent = getIntent();
        PhoneNumber = intent.getStringExtra("PhoneNumber");

        //vibrate and beep
        double[] c = {1,800};
        final Beep beep = new Beep(c,this);
        handler = new Handler();
        vibrateRunnable = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                handler.postDelayed(this,1000);
                count += 1;
                vibrator.vibrate(1000);
                beep.play();
                if(count == 5){
                    handler.removeCallbacks(this);
                }
            }
        };
    }

    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(TumbleService.TUMBLE_RESULT));
    }

    protected void onStop(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver((broadcastReceiver));
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startService(){
        Intent intent = new Intent(DetectActivity.this, TumbleService.class);
        startService(intent);
    }

    private TumbleBinder myBinder;
    //service connection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (TumbleBinder) service;
            myBinder.startLog();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public void bindService() {
        Intent intent = new Intent(this, TumbleService.class);
        bindService(intent, connection, 0);
    }



    public void handleTumbleResult(String s){
        if(s.equals("false")){
            status.setText(getString(R.string.tumble_not_detected));
        }else if(s.equals("true")){
            handler.postDelayed(vibrateRunnable,100);
            CancelLayout.setVisibility(View.VISIBLE);
        }else if(s.equals("processing")){
            status.setText(getString(R.string.start_processing));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                vibrator.cancel();
                handler.removeCallbacks(vibrateRunnable);
        }
        return false;
    }
}
