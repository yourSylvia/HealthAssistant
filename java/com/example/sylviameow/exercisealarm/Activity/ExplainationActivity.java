package com.example.sylviameow.exercisealarm.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.Reports.HurtSelfReportActivity;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class ExplainationActivity extends AppCompatActivity {

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explaination);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initRealm();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();
        final long duration = bundle.getLong("duration");
//        final int rate = bundle.getInt("rate");
//        Toast.makeText(this, rate+"", Toast.LENGTH_SHORT).show();

        /* add the duration to DB */
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

                    // exercise duration stored to DB in minutes
                    if(id_result.size() == 0){
                        UserState userState = realm.createObject(UserState.class, generateID());
                        getUserId();
                        userState.setUser_id(user_id);
                        userState.setDate(getCurrentDate());
                        userState.setDay_count(1);
                        userState.setExercise_count((int) duration/1000/60);
                    }
                    else{
                        UserState hurt_query = realm.where(UserState.class)
                                .equalTo("id",generateID())
                                .findFirst();

                        int old_count = hurt_query.getExercise_count();
                        hurt_query.setDay_count(1);
                        hurt_query.setExercise_count(old_count + (int) duration/1000/60);
                        Toast.makeText(ExplainationActivity.this,
                                "Update exercise count",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        finally {
            if (realm != null) {
                realm.close();
            }
        }
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


    protected void initView(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.open_video);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ExplainationActivity.this, PlayerActivity.class);
                startActivityForResult(i,0);
            }
        });

        ImageView img1 = (ImageView) findViewById(R.id.exercise_preview1);
        ImageView img2 = (ImageView) findViewById(R.id.exercise_preview2);

        img1.setImageResource(R.drawable.video_1_1);
        img2.setImageResource(R.drawable.video_1_2);
    }


    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
