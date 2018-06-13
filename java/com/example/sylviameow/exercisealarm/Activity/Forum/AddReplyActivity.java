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

public class AddReplyActivity extends AppCompatActivity {

    private EditText comment;
    private Button submit_btn;

    private String user_id;

    private int ques_id;
    private int comment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reply);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /* Get question and max comment id from previous page */
        ques_id = getIntent().getIntExtra("ques_id", -1);
        comment_id = getIntent().getIntExtra("max_comment_id", -1);

        initView();
        initRealm();
        getUserInfoFromDB();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(postComment).start();
            }
        });
    }


    Runnable postComment = new Runnable() {
        @Override
        public void run() {
            String comment_discuss_id = String.valueOf(comment_id+1);

            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/comment/insert?"
                    + "comment_discuss_id="
                    + ques_id
                    + "&comment_comment_id="
                    + comment_discuss_id
                    + "&comment_user_id="
                    + user_id
                    + "&comment_content="
                    + comment.getText().toString();

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
                            Toast.makeText(AddReplyActivity.this, "添加評論成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent i = new Intent(AddReplyActivity.this, MyQuesDetailsActivity.class);
                    i.putExtra("Ques_id", ques_id);
                    startActivity(i);
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
                    /* must login first */
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
        comment = (EditText) findViewById(R.id.comment_input);
        submit_btn = (Button) findViewById(R.id.submit_btn);
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
