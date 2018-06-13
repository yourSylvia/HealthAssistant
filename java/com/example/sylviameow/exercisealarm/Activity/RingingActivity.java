package com.example.sylviameow.exercisealarm.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sylviameow.exercisealarm.R;

public class RingingActivity extends AppCompatActivity {
    private Button close;
    private ImageView imageView;
    MediaPlayer mediaPlayer;
    private long startTime;

    int alarmId;

    private SharedPreferences sp_alarm_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);

        initview();

        imageShow();

        playRingtone();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingingActivity.this, MainActivity.class);
                startActivity(intent);

                final Intent piIntent = new Intent(getApplicationContext(), RingingActivity.class);
                piIntent.putExtra("alarmId", alarmId);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),alarmId, piIntent,0);
                AlarmManager alarmManager = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
                long timeUp = startTime + 1000 * 3600 * 24;
                alarmManager.set(AlarmManager.RTC_WAKEUP,timeUp, pi);

                mediaPlayer.stop();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    protected void imageShow() {
        String temp = sp_alarm_setting.getString("img_uri_" + alarmId, "000");
        Log.i(getClass().getName(), temp);
        try {
            Uri uri = Uri.parse(temp);

            if (temp.equals("000")) {
                imageView.setImageResource(R.drawable.sports);
            } else {
                imageView.setImageURI(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("Exception thrown  :" + e);
        }
    }

    private void playRingtone() {
        String temp = sp_alarm_setting.getString("ringtone_uri_" + alarmId, "000");
        if (temp.equals("000")) {
            mediaPlayer = MediaPlayer.create(RingingActivity.this, R.raw.ring);
        }else{
            Uri uri = Uri.parse(temp);
            mediaPlayer = MediaPlayer.create(RingingActivity.this, uri);

        }

        mediaPlayer.start();
    }

    private void initview() {
        startTime = System.currentTimeMillis();
        close = (Button) findViewById(R.id.alarm_ring_close);
        imageView = (ImageView) findViewById(R.id.customize_image);
        sp_alarm_setting = RingingActivity.this.getSharedPreferences("alarm_setting", MODE_APPEND);
        alarmId = getIntent().getIntExtra("alarmId",0);
    }
}
