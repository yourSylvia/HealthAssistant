package com.example.sylviameow.exercisealarm.Activity.Forum;

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
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ConsultActivity extends AppCompatActivity {

    private EditText title;
    private EditText content;
    private Button submit_btn;

    private String discuss_title;
    private String discuss_content;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initRealm();
        initView();

        buttonEvent();
    }


    protected void buttonEvent(){
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInfoFromDB();

                discuss_title = title.getText().toString();
                discuss_content = content.getText().toString();

                // New thread
                new Thread(postQuestion).start();

                Intent i = new Intent(ConsultActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }


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
                }
            });
        }
        finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


    Runnable postQuestion = new Runnable() {
        @Override
        public void run() {
            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/discuss/insert?"
                    + "&discuss_title="
                    + discuss_title
                    + "&discuss_content="
                    + discuss_content
                    + "&discuss_user_id="
                    + user_id;

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();

                JSONObject jsonObject = new JSONObject(docJSON.text());

                String status = jsonObject.getString("status");

                if(status.equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConsultActivity.this, "發布成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConsultActivity.this, "出錯了，請再試一次", Toast.LENGTH_SHORT).show();
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


    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }


    protected void initView(){
        title = (EditText) findViewById(R.id.input_title);
        content = (EditText) findViewById(R.id.input_content);
        submit_btn = (Button) findViewById(R.id.submit_btn);
    }
}
