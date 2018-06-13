package com.example.sylviameow.exercisealarm.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.Login.ChangePSWActivity;
import com.example.sylviameow.exercisealarm.Activity.Login.LoginActivity;
import com.example.sylviameow.exercisealarm.Database.AlarmInfo;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class SettingActivity extends AppCompatActivity {

    private LinearLayout change_btn;
    private LinearLayout help_btn;
    private LinearLayout aboutus_btn;
    private Button logout_btn;

    private String user_data_json;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initRealm();

        buttonEvent();

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


    protected void buttonEvent(){
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(SettingActivity.this, ChangePSWActivity.class);
                startActivity(i1);
            }
        });

        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(SettingActivity.this, HelpActivity.class);
                startActivity(i2);
            }
        });

        aboutus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(SettingActivity.this, AboutUsActivity.class);
                startActivity(i3);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SettingActivity.this).setMessage("確認退出登錄?")
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                /* Clear shared preferences */
                                SharedPreferences sharedPreferences =
                                        SettingActivity.this.getSharedPreferences("login_information", MODE_APPEND);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("login_state","False");
                                editor.apply();

                                /* Save DB to server */
                                generateUserDataJSON();
                                getUserId();
                                new Thread(uploadDB).start();

                                /* Clear DB */
//                                clearDB();

                                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                                startActivity(intent);

                                Toast.makeText(SettingActivity.this, "已登出", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });
    }


    Runnable uploadDB = new Runnable() {
        @Override
        public void run() {
            String url= "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/upload_data?user_id="
            + user_id
            + "&user_data="
            + user_data_json;

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();
                JSONObject jsonObject = new JSONObject(docJSON.text());

                String status = jsonObject.get("status").toString();

                if(!status.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SettingActivity.this, "備份數據失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }
    };


    protected void generateUserDataJSON(){
        Realm realm = null;
        JSONObject object = new JSONObject();

        try{
            realm = Realm.getDefaultInstance();
            RealmResults<AlarmInfo> alarmInfos = realm.where(AlarmInfo.class)
                    .findAll();

            RealmResults<UserState> userStates = realm.where(UserState.class)
                    .findAll();

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            for(int i=0; i<alarmInfos.size(); i++){
                jsonObject.put("alarm_id", alarmInfos.get(i).getAlarm_id());
                jsonObject.put("timestamp", alarmInfos.get(i).getTime_stamp());
                jsonObject.put("time", alarmInfos.get(i).getTime());
                jsonArray.put(jsonObject);
            }
            object.put("alarmInfo", jsonArray);

            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
            for(int i=0; i<userStates.size(); i++){
                jsonObject.put("id",userStates.get(i).getId());
                jsonObject.put("date", userStates.get(i).getDate());
                jsonObject.put("exercise_count", userStates.get(i).getExercise_count());
                jsonObject.put("star_count",userStates.get(i).getStar_count());
                jsonObject.put("day_count",userStates.get(i).getDay_count());
                jsonObject.put("Current_hurt_count",userStates.get(i).getCurrent_hurt_count());

                jsonArray.put(jsonObject);
            }
            object.put("userStates", jsonArray);

            String s = object.toString();
            user_data_json = URLEncoder.encode(s, "UTF-8");
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            Log.e("Yourapp", "UnsupportedEncodingException");
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }
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


    protected void clearDB(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
        realm.close();
    }


    protected void initView(){
        change_btn = (LinearLayout) findViewById(R.id.change_btn);
        help_btn = (LinearLayout) findViewById(R.id.help_btn);
        aboutus_btn = (LinearLayout) findViewById(R.id.aboutus_btn);
        logout_btn = (Button) findViewById(R.id.logout_btn);
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
