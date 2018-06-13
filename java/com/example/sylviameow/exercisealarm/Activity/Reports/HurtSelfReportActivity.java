package com.example.sylviameow.exercisealarm.Activity.Reports;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.MainActivity;
import com.example.sylviameow.exercisealarm.Database.HurtReportInfo;
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

public class HurtSelfReportActivity extends AppCompatActivity
{
    private Button submit_btn;
    private EditText input;

    private int max_id;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hurt_self_report);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initRealm();

        buttonEvent();
    }


    protected void buttonEvent(){
        submit_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /* verify input */
                String hurt_input = input.getText().toString();

                if(Integer.parseInt(hurt_input) > 10){
                    Toast.makeText(HurtSelfReportActivity.this, "請輸入數字0-10", Toast.LENGTH_SHORT).show();
                }
                else{
                    /* Check the ID is existing or not */
                    Realm realm=Realm.getDefaultInstance();
                    RealmResults<UserState> id_result = realm.where(UserState.class)
                            .equalTo("id",generateID())
                            .findAll();

                    /* add to hurtReport DB*/
                    try{
                        realm.beginTransaction();
                        max_id = realm.where(HurtReportInfo.class)
                                .findAll()
                                .max("id")
                                .intValue();
                    }
                    catch (NullPointerException e){
                        max_id = 0;
                    }

                    /* format the time in DB */
                    HurtReportInfo hurtReportInfo = realm.createObject(HurtReportInfo.class,
                            max_id+1);
                    hurtReportInfo.setDate(getCurrentDate());
                    hurtReportInfo.setTimestamp(""+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                            +":"+Calendar.getInstance().get(Calendar.MINUTE));
                    hurtReportInfo.setHurt_level(Integer.valueOf(input.getText().toString()));
                    realm.commitTransaction();

                    if(id_result.size() == 0)
                    {
                        /* Add the info into myState DB */
                        realm.beginTransaction();
                        UserState userState = realm.createObject(UserState.class, generateID());
                        getUserId();
                        userState.setUser_id(user_id);
                        userState.setDate(getCurrentDate());
                        userState.setCurrent_hurt_count(Integer.valueOf(hurt_input));
                        Toast.makeText(HurtSelfReportActivity.this,
                                "Set hurt level " + Integer.valueOf(hurt_input),
                                Toast.LENGTH_SHORT).show();
                        realm.commitTransaction();
                    }
                    else
                    {
                        /* Update myState DB */
                        double hurt_daily_avg = realm.where(HurtReportInfo.class)
                                .equalTo("date", getCurrentDate())
                                .findAll()
                                .average("hurt_level");

                        UserState hurt_query = realm.where(UserState.class)
                                .equalTo("id",generateID())
                                .findFirst();

                        realm.beginTransaction();
                        hurt_query.setCurrent_hurt_count((int) (hurt_daily_avg+0.5));
                        Toast.makeText(HurtSelfReportActivity.this,
                                "Update hurt level",
                                Toast.LENGTH_SHORT).show();
                        realm.commitTransaction();
                    }

                    realm.close();

                    Intent intent = new Intent(HurtSelfReportActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    public String getCurrentDate(){
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void initView()
    {
        submit_btn = (Button) findViewById(R.id.submit_btn);
        input = (EditText) findViewById(R.id.hurt_level_input);
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
