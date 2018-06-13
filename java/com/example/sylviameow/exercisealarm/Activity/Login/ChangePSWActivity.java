package com.example.sylviameow.exercisealarm.Activity.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.MainActivity;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ChangePSWActivity extends AppCompatActivity {

    private Button submit_btn;
    private EditText old_psw_input;
    private EditText new_psw_input;
    private EditText con_psw_input;

    private String user_id;
    private String username;

    private String old_psw;
    private String new_psw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_psw);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initRealm();

        getUserInfoFromDB();

        submitChange();

    }


    protected void submitChange(){
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Check the old password*/
                old_psw = old_psw_input.getText().toString();
                // new thread
                new Thread(checkOldPassword).start();
            }
        });
    }


    protected void confirmDialog() {
        new AlertDialog.Builder(ChangePSWActivity.this).setMessage("確認更改密碼?")
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // New thread
                        new Thread(updatePassword).start();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

    }


    Runnable checkOldPassword = new Runnable() {
        @Override
        public void run() {
            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/signin?user_username=" +
                    username +
                    "&user_password=" +
                    old_psw;

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();
                JSONObject jsonObject = new JSONObject(docJSON.text());

                String verification = jsonObject.getString("status");

                if(verification.equals("true")){
                    /* Check the new password*/
                    new_psw = new_psw_input.getText().toString();
                    String con_psw = con_psw_input.getText().toString();

                    if(new_psw.equals(con_psw)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                confirmDialog();
                            }
                        });
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChangePSWActivity.this, "確認密碼和新密碼需要一致", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePSWActivity.this, "舊密碼輸入錯誤",Toast.LENGTH_SHORT).show();
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


    Runnable updatePassword = new Runnable() {
        @Override
        public void run() {
            new_psw = new_psw_input.getText().toString();

            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/update_password?user_id=" +
                    user_id +
                    "&user_old_password=" +
                    old_psw +
                    "&user_password=" +
                    new_psw;

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();
                JSONObject jsonObject = new JSONObject(docJSON.text());

                String status = jsonObject.getString("status");

                if(status.equals("true")){
                    Intent intent = new Intent(ChangePSWActivity.this, MainActivity.class);
                    startActivity(intent);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePSWActivity.this, "更改成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChangePSWActivity.this, "出錯了，請再試一次", Toast.LENGTH_SHORT).show();
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


    protected void getUserInfoFromDB(){
        Realm realm = null;

        try{
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    int userServerInfo_id = realm.where(UserServerInfo.class)
                            .findAll()
                            .max("id")
                            .intValue();

                    RealmResults<UserServerInfo> results = realm.where(UserServerInfo.class)
                            .equalTo("id", userServerInfo_id)
                            .findAll();

                    user_id = results.get(0).getUser_id();
                    username = results.get(0).getUser_username();

                }
            });
        }
        finally {
            if (realm != null) {
                realm.close();
            }
        }
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


    protected void initView(){
        submit_btn = (Button) findViewById(R.id.submit_btn);

        old_psw_input = (EditText) findViewById(R.id.origin_psw);

        new_psw_input = (EditText) findViewById(R.id.new_psw);

        con_psw_input = (EditText) findViewById(R.id.confirm_psw);

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
