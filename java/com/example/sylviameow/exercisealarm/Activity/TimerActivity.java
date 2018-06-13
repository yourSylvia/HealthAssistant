package com.example.sylviameow.exercisealarm.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.Result;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class TimerActivity extends AppCompatActivity {

    protected String time;
    private String user_id;

    private Timer timer = null;
    private TimerTask task = null;
    private Message msg = null;
    private Handler handler;

    private TextView minute;
    private TextView second;
    private TextView nano_sec;
    private int min = 0;
    private int sec = 0;
    private int nano = 0;

    private LinearLayout timer_btn;
    private TextView t1;
    private TextView t2;

    private boolean isStart = false;
    private boolean isPulse = false;
//    private boolean isSubmit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }


    protected void initView() {

        String initial  = "00";

        // minute, second, nano_sec
        minute = (TextView) findViewById(R.id.minute);
        second = (TextView) findViewById(R.id.second);
        nano_sec = (TextView) findViewById(R.id.nano_sec);
        minute.setText(initial);
        second.setText(initial);
        nano_sec.setText(initial);

        // Handler to update UI
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String initial = "00";
                String temp;

                if(nano == 99){
                    nano_sec.setText(initial);
                    nano = 0;
                    if(sec == 60){
                        second.setText(initial);
                        sec = 0;
                        min++;
                        temp = min < 10 ? "0" + Integer.toString(min) : Integer.toString(min);
                        minute.setText(temp);
                    }else{
                        sec++;
                        temp = sec < 10 ? "0" + Integer.toString(sec) : Integer.toString(sec);
                        second.setText(temp);
                    }
                }
                else{
                    nano++;
                    temp = nano < 10? "0" + Integer.toString(nano) : Integer.toString(nano);
                    nano_sec.setText(temp);
                }
            }
        };

        // Button changes
        timer_btn = (LinearLayout) findViewById(R.id.timer_btn);
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);

        t1.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        t1.setBackgroundResource(R.color.LightRed);
        t2.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        t2.setBackgroundResource(R.color.LightRed);
        t1.setText("開始");
        t2.setText("計時");

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isStart = true;
                clickEventLeft();
            }
        });

        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isStart){
                    showDialog();
                }
                isStart = true;
                clickEventLeft();
            }
        });
    }


    protected void clickEventLeft(){
        if (isStart && !isPulse) {
            // start
            onCounterStart();

            t1.setGravity(Gravity.CENTER);
            t1.setBackgroundResource(R.color.LightAlphaRed);
            t2.setGravity(Gravity.CENTER);
            t2.setBackgroundResource(R.color.LightRed);

            t1.setText("暫停計時");
            t2.setText("完成鍛鍊並提交");

            isPulse = true;
        }
        else if(isStart && isPulse){
            // pulse
            onCounterPulse();

            t1.setGravity(Gravity.CENTER);
            t1.setBackgroundResource(R.color.LightAlphaRed);
            t2.setGravity(Gravity.CENTER);
            t2.setBackgroundResource(R.color.LightRed);

            t1.setText("繼續鍛鍊");
            t2.setText("完成鍛鍊並提交");

            isPulse = false;
        }
        else if(!isStart && !isPulse){
            // restart
            onCounterStart();

            t1.setText("暫停計時");
            t2.setText("完成鍛鍊並提交");
        }
    }


    protected void onCounterStart(){
        task = new TimerTask() {
            @Override
            public void run() {
                msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        timer = new Timer();
        timer.schedule(task, 0,10);
    }


    protected void onCounterPulse(){
        task.cancel();
        timer.cancel();
    }


    protected void showDialog() {
        new AlertDialog.Builder(TimerActivity.this).setMessage("確認提交鍛鍊?")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Save to DB
                        Realm realm = null;
                        try {
                            realm = Realm.getDefaultInstance();
                            RealmResults<UserState> result = realm.where(UserState.class)
                                    .equalTo("id", generateID())
                                    .findAll();

                            if(result.size() == 0){
                                realm.beginTransaction();
                                UserState userState = realm.createObject(UserState.class, generateID());
                                userState.setUser_id(user_id);
                                userState.setDay_count(1);
                                userState.setDate(getCurrentDate());
                                if(sec >= 30){
                                    userState.setExercise_count(min + 1);
                                }
                                else{
                                    userState.setExercise_count(min);
                                }
                                realm.commitTransaction();
                            }
                            else{
                                int old_count = result.get(0).getExercise_count();

                                realm.beginTransaction();
                                if(sec >= 30){
                                    result.get(0).setDay_count(1);
                                    result.get(0).setExercise_count(old_count + min + 1);
                                }
                                else{
                                    result.get(0).setDay_count(1);
                                    result.get(0).setExercise_count(old_count + min);
                                }
                                realm.commitTransaction();
                            }
                        }
                        finally {
                            if (realm != null) {
                                realm.close();
                            }
                        }

                        Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }


    public long generateID(){
        long id;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

        Calendar c = Calendar.getInstance();
        id = Long.valueOf(formater.format(c.getTime()));

        return id;
    }


    protected void getUserId(){
        Realm realm = null;
        try{
            realm = Realm.getDefaultInstance();
            RealmResults<UserServerInfo> result = realm.where(UserServerInfo.class)
                    .findAll()
                    .sort("id", Sort.DESCENDING);

            if(result.size() != 0){
                user_id = result.get(0).getUser_id();
            }
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }
    }


    public String getCurrentDate(){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
    }


    protected void initRealm() {
        Realm.init(TimerActivity.this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
