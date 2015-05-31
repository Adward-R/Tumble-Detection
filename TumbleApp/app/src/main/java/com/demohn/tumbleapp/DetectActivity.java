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
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class DetectActivity extends ActionBarActivity{

    //receive broadcasts from service
    BroadcastReceiver broadcastReceiver;
    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        status = (TextView) findViewById(R.id.status);
        //beep
        double num[] ={1,600};
        Beep beep = new Beep(num);
        beep.play();

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

    public void h(){

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
        Log.d("ff", s);
        if(s.equals("false")){
            status.setText(getString(R.string.tumble_not_detected));
        }else if(s.equals("true")){
            status.setText(getString(R.string.tumble_detected));
        }else if(s.equals("processing")){
            status.setText(getString(R.string.start_processing));
        }
    }
}
