package com.example.sylviameow.exercisealarm.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class RecorderActivity extends AppCompatActivity {

    private SharedPreferences sp_alarm_setting;

    private Button start;// 开始录制按钮

    private int alarmId;

    private MediaRecorder mediarecorder;// 录制视频的类

    private SurfaceView surfaceview;// 显示视频的控件

    private File mAudioFile;

    private TextView txtRecordStatus;

    private Boolean isRecordStarted = false;
// 用来显示视频的一个接口，我靠不用还不行，也就是说用mediarecorder录制视频还得给个界面看

// 想偷偷录视频的同学可以考虑别的办法。。嗯需要实现这个接口的Callback接口


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,

                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

// 设置横屏显示

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

// 选择支持半透明模式,在有surfaceview的activity中使用。

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.activity_recorder);

        init();

    }

    @SuppressLint("WrongConstant")
    private void init() {

        start = (Button) this.findViewById(R.id.start);
        txtRecordStatus = this.findViewById(R.id.txtRecordState);

        start.setOnClickListener(new TestVideoListener());

        sp_alarm_setting = getApplicationContext().getSharedPreferences("alarm_setting", Context.MODE_APPEND);
        alarmId = getIntent().getIntExtra("alarmId",0);


    }

    class TestVideoListener implements View.OnClickListener {

        @Override

        public void onClick(View v) {

            if (!isRecordStarted) {
                mAudioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio/" + System.currentTimeMillis() + ".mp3");
                mAudioFile.getParentFile().mkdirs();

                mediarecorder = new MediaRecorder();// 创建mediarecorder对象

// 设置录制视频源为Camera(相机)

                mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4

                mediarecorder

                        .setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);

// 设置录制的视频编码h263 h264

                mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


                mediarecorder.setAudioEncodingBitRate(96000);
                mediarecorder.setAudioSamplingRate(44100);

// 设置视频文件输出的路径

                mediarecorder.setOutputFile(mAudioFile.getAbsolutePath());

                try {

                    // 准备录制

                    mediarecorder.prepare();

                    // 开始录制

                    mediarecorder.start();
                    txtRecordStatus.setText(R.string.on_record);
                    start.setText(R.string.stop_record);
                    isRecordStarted = true;

                } catch (IllegalStateException e) {

                     // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IOException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                }

            } else {

                if (mediarecorder != null) {


                    mediarecorder.stop();
                    txtRecordStatus.setText(R.string.recorded);


                    mediarecorder.release();

                    SharedPreferences.Editor editor = sp_alarm_setting.edit();
                    editor.putString("ringtone_uri_"+alarmId, mAudioFile.getAbsolutePath());
                    editor.apply();

                    startActivity(new Intent(RecorderActivity.this, AlarmSetUp.class));
                    Toast.makeText(getApplicationContext(), "Recorded successfully  .", Toast.LENGTH_LONG).show();
                    mediarecorder = null;

                }

            }

        }

    }


}

