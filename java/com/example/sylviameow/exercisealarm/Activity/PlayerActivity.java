package com.example.sylviameow.exercisealarm.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class PlayerActivity extends AppCompatActivity implements RatingDialogListener {

    long startTime;
    VideoView videoView;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        startTime = new Date().getTime();

        initView();
    }

    @Override
    public void onPositiveButtonClicked(final int rate, String comment) {
        // interpret results, send it to analytics etc...
        Realm realm = null;
        try{
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    /* Check the ID is existing or not */
                    RealmResults<UserState> id_result = realm.where(UserState.class)
                            .equalTo("id",generateID())
                            .findAll();

                    if(id_result.size() == 0){
                        UserState userState = realm.createObject(UserState.class, generateID());
                        getUserId();
                        userState.setDate(getCurrentDate());
                        userState.setUser_id(user_id);
                        userState.setStar_count(rate);
                    }
                    else{
                        int old_rate = id_result.get(0).getStar_count();
                        id_result.get(0).setStar_count(old_rate+rate);
                    }
                }
            });
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }


        Intent intent = new Intent();
        intent.putExtra("duration", new Date().getTime() - startTime);
//        intent.putExtra("rate", rate);

        setResult(RESULT_OK, intent);
        finish();
        // 返回上一界面并储存星星数量
    }

    @Override
    public void onNegativeButtonClicked() {
        // 继续播放视频
//        videoView.start();

        // 返回上一界面
        Intent i = new Intent(PlayerActivity.this, ExplainationActivity.class);
        i.putExtra("duration", 0);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onNeutralButtonClicked() {


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            showDialog();
            videoView.pause();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public String getCurrentDate(){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
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


    public long generateID(){
        long id;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

        Calendar c = Calendar.getInstance();
        id = Long.valueOf(formater.format(c.getTime()));

        return id;
    }


    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("提交")
                .setNegativeButtonText("取消")
                .setNoteDescriptions(Arrays.asList("很糟", "不是很好", "一般", "好", "非常好 !!!"))
                .setTitle("給自己打個分數吧")
                .setStarColor(R.color.orange)
                .setTitleTextColor(R.color.gray)
                .create(PlayerActivity.this)
                .show();
    }


    protected void initView() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
//        videoView.setVideoPath("file:///android_asset/video.mp4");
        videoView.start();
        videoView.requestFocus();

    }


    protected void initRealm()
    {
        Realm.init(this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
