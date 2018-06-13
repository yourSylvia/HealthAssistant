package com.example.sylviameow.exercisealarm.Activity.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.MainActivity;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class LoginActivity extends AppCompatActivity{

    EditText username, password;
    Button register_btn;
    Button sign_in_btn;
    TextView link_to_hint;
    String verification;

    private String user_id, user_username, user_number, user_nickname, user_platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        initRealm();

        forgetPsw();

        attemptLogin();
    }


    Runnable networkTask = new Runnable() {
        @Override
        public void run() {

            String uname = username.getText().toString();
            String upassword = password.getText().toString();

            String url_string = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/signin?user_username="
                    + uname
                    + "&user_password="
                    + upassword;

            try{
                Document doc = Jsoup.connect(url_string)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();
                JSONObject jsonObject = new JSONObject(docJSON.text());

                String verification = jsonObject.getString("status");
                user_id = jsonObject.getJSONObject("data").getString("user_id");
                user_username = jsonObject.getJSONObject("data").getString("user_username");
                user_number = jsonObject.getJSONObject("data").getString("user_number");
                user_nickname = jsonObject.getJSONObject("data").getString("user_nickname");
                user_platform = jsonObject.getJSONObject("data").getString("user_platform");

                if(verification.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);

                            writeToDB();
                            writeToPreferences("true");
                        }
                    });
                }
                else{
                    final String msg = jsonObject.getString("msg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            writeToPreferences("false");
                            Toast.makeText(LoginActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
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


    protected void attemptLogin(){

        sign_in_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Verification process*/
                new Thread(networkTask).start();

            }
        });
    }


    protected void forgetPsw(){
        link_to_hint.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, ContactUs.class);
                startActivity(intent);
            }
        });
    }


    protected void writeToPreferences(String verification){
        SharedPreferences sharedPreferences =
                getApplicationContext().getSharedPreferences("login_information", MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_state",verification);
        editor.apply();
    }


    protected void writeToDB() {
        /* Write user_id to DB */
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    try{
                        int userServerInfo_id = realm.where(UserServerInfo.class)
                                .findAll()
                                .max("id")
                                .intValue();

                        RealmResults<UserServerInfo> result = realm.where(UserServerInfo.class)
                                .equalTo("user_id",user_id)
                                .findAll();

                        if(result.size() == 0){
                            UserServerInfo userServerInfo = realm.createObject(UserServerInfo.class, user_id);

                            userServerInfo.setId(userServerInfo_id+1);
                            userServerInfo.setUser_username(user_username);
                            userServerInfo.setUser_number(user_number);
                            userServerInfo.setUser_nickname(user_nickname);
                            userServerInfo.setUser_platform(user_platform);
                        }
                        else{
                            UserServerInfo userServerInfo_user_id = realm.where(UserServerInfo.class)
                                    .equalTo("user_id", user_id)
                                    .findFirst();
                            userServerInfo_user_id.setId(userServerInfo_id+1);
                        }
                    }
                    catch (NullPointerException e){
                        UserServerInfo userServerInfo = realm.createObject(UserServerInfo.class, user_id);

                        userServerInfo.setId(0);
                        userServerInfo.setUser_username(user_username);
                        userServerInfo.setUser_number(user_number);
                        userServerInfo.setUser_nickname(user_nickname);
                        userServerInfo.setUser_platform(user_platform);
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


    protected void initView(){
        verification = "default";

        register_btn = (Button) findViewById(R.id.register_btn) ;
        register_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, EnterPhoneNumActivity.class);
                startActivity(i);
            }
        });

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.psw);
        sign_in_btn = (Button) findViewById(R.id.sign_in_btn);
        link_to_hint = (TextView) findViewById(R.id.link_to_hint);
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

