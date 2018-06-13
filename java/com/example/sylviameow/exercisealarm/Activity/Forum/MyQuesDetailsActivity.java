package com.example.sylviameow.exercisealarm.Activity.Forum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.myBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyQuesDetailsActivity extends AppCompatActivity {

    private TextView ques_content;
    private TextView expert_reply;
    private LinearLayout add_reply_btn;

    private String default_msg;
    private String ques_detail;
    private String expert_re;

    List<Map> user_reply_list_string;

    private int ques_id;
    private int comt_id = -1;
    private myBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ques_details);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        /* Get question id from previous page */
        ques_id = getIntent().getIntExtra("Ques_id",-1);

        if(ques_id == -1){
            ques_content.setText(default_msg);
            expert_reply.setText(default_msg);
        }
        else{
            new Thread(getGetForumDetails).start();
        }

    }


    Runnable getGetForumDetails = new Runnable() {
        @Override
        public void run() {
            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/discuss/select/detail/" + ques_id;

            try {
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();

                JSONObject jsonObject = new JSONObject(docJSON.text());

                // get question detail and expert reply
                ques_detail = jsonObject.getJSONObject("data").getString("content");
                expert_re = jsonObject.getJSONObject("data").getString("expert");

                // Get Comment list
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("comment");
                user_reply_list_string = new ArrayList<>();

                for(int i=0; i<jsonArray.length();i++){
                    HashMap<String, String> map = new HashMap<String, String>();

                    String id = jsonArray.getJSONObject(i).getString("id");
                    String username = jsonArray.getJSONObject(i).getString("user_nickname");
                    String comment = jsonArray.getJSONObject(i).getString("content");
                    String date = jsonArray.getJSONObject(i).getString("create_date");

                    if(Integer.parseInt(id) > comt_id){
                        comt_id = Integer.parseInt(id);
                    }

                    map.put(id, username + "-" + comment + "-" + date);
                    user_reply_list_string.add(map);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ques_content.setText(ques_detail);
                        expert_reply.setText(expert_re);
                    }
                });
                Log.e("MYAPP", "unexpected JSON exception", e);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ques_content.setText(ques_detail);
                    expert_reply.setText(expert_re);

                    writeToReply();
                }
            });

        }
    };


    protected void writeToReply(){

        ListView user_reply_list_view = (ListView) findViewById(R.id.my_ques_reply_list);

        adapter = new myBaseAdapter(MyQuesDetailsActivity.this, user_reply_list_string, 6);

        user_reply_list_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        user_reply_list_view.requestFocusFromTouch();

    }


    private void initView(){

        ques_content = (TextView) findViewById(R.id.ques_content);
        expert_reply = (TextView) findViewById(R.id.expert_reply);
        add_reply_btn = (LinearLayout) findViewById(R.id.add_reply_btn);

        add_reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyQuesDetailsActivity.this, AddReplyActivity.class);
                intent.putExtra("ques_id", ques_id);
                intent.putExtra("max_comment_id", comt_id);
                startActivity(intent);
            }
        });

        default_msg = "NaN";

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


}
