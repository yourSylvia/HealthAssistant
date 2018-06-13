package com.example.sylviameow.exercisealarm.Activity.Forum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
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

import io.realm.Realm;
import io.realm.RealmResults;

public class MyQuestionActivity extends AppCompatActivity {

    private List<Map> ques_title_string_list;
    private ListView ques_title_view_list;
    private int pos;

    private String user_id;

    private myBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getUserInfoFromDB();

        new Thread(getForumGeneral).start();

    }


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


    Runnable getForumGeneral = new Runnable() {

        @Override
        public void run() {
            ques_title_string_list = new ArrayList<>();
            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/discuss/select/user/"
                    + user_id
                    + "?discuss_offset=0&discuss_page_size=20";

            try {
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();

                JSONObject jsonObject = new JSONObject(docJSON.text());

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));

                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String title = jsonArray.getJSONObject(i).getString("title");
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(id, title);
                    ques_title_string_list.add(map);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        writeToQuestion();
                    }
                });

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }

    };


    protected void writeToQuestion(){

        ques_title_view_list = (ListView) findViewById(R.id.my_ques_list);

        adapter = new myBaseAdapter(MyQuestionActivity.this, ques_title_string_list, 5);

        ques_title_view_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ques_title_view_list.requestFocusFromTouch();

        itemClicked();
    }


    protected void itemClicked(){

        ques_title_view_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                pos = adapterView.getPositionForView(view);
                adapter = new myBaseAdapter(MyQuestionActivity.this, ques_title_string_list, 5);
                String id = adapter.getItem(pos).toString();
                Toast.makeText(MyQuestionActivity.this, "Index" + id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MyQuestionActivity.this, MyQuesDetailsActivity.class);
                intent.putExtra("Ques_id",Integer.parseInt(id));
                startActivity(intent);

            }
        });

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
