package com.example.sylviameow.exercisealarm.Activity.Reports;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Adapters.VideoListAdapter;
import com.example.sylviameow.exercisealarm.Adapters.starRankAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class ReportStars extends AppCompatActivity {

    private TextView stat_txt;
    private String user_id;

    private TextView user_rank;
    private TextView total_users;
    private ListView star_rank_view;

    private String user_rank_t;
    private String total_users_t;
    private List<Map> user_rank_list = new ArrayList<>();

    starRankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_stars);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        new Thread(getUserRank).start();
    }


    Runnable getUserRank = new Runnable() {
        @Override
        public void run() {
            getUserId();
            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/user_star?user_id="
                    + user_id;

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();
                JSONObject jsonObject = new JSONObject(docJSON.text());

                // Display user rank
                user_rank_t = jsonObject.getJSONObject("data").getString("user_no");
                total_users_t = jsonObject.getJSONObject("data").getString("star_total");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user_rank.setText(user_rank_t);
                        String s = " / " + total_users_t;
                        total_users.setText(s);
                    }
                });

                // Display user rank list
                JSONArray jsonArray = new JSONArray(jsonObject
                        .getJSONObject("data")
                        .getString("star_list"));

                for(int i=0; i<jsonArray.length(); i++){
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String user_nickname = jsonArray.getJSONObject(i).getString("user_nickname");
                    String star_num = jsonArray.getJSONObject(i).getString("star_num");

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(id, String.valueOf(i+1) + "-" + user_nickname + "-" + star_num);
                    user_rank_list.add(map);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        writeToVideoList();
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


    protected void writeToVideoList() {
        adapter = new starRankAdapter(ReportStars.this, user_rank_list);

        star_rank_view.setAdapter(adapter);
    }


    protected int getStarCount(){
        int star;

        Realm realm = null;
        try{
            realm = Realm.getDefaultInstance();
            star = realm.where(UserState.class)
                    .findAll()
                    .sum("star_count")
                    .intValue();
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }

        return star;
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
        stat_txt = (TextView) findViewById(R.id.star_txt);
        stat_txt.setText(String.valueOf(getStarCount()));

        user_rank = (TextView) findViewById(R.id.user_rank);
        total_users = (TextView) findViewById(R.id.total_users);

        star_rank_view = (ListView) findViewById(R.id.star_rank_list);
    }


    protected void initRealm() {
        Realm.init(ReportStars.this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
