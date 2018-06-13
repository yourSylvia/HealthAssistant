package com.example.sylviameow.exercisealarm.Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.Reports.HurtSelfReportActivity;
import com.example.sylviameow.exercisealarm.Activity.SettingActivity;
import com.example.sylviameow.exercisealarm.Activity.TimerActivity;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Activity.Reports.ReportDays;
import com.example.sylviameow.exercisealarm.Activity.Reports.ReportExercise;
import com.example.sylviameow.exercisealarm.Activity.Reports.ReportScores;
import com.example.sylviameow.exercisealarm.Activity.Reports.ReportStars;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class MyState extends Fragment {
    private LinearLayout exercise_btn;
    private LinearLayout stars_btn;
    private LinearLayout days_btn;
    private LinearLayout scores_btn;
    private LinearLayout self_report_btn;
    private LinearLayout counter_btn;

    private TextView exercise_unit;
    private Realm realm = null;

    private String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_my_state, null);

        initRealm();

        initView(view);

        buttonEvent();

        setHasOptionsMenu(true);

        getUserId();
        new Thread(uploadStarCnt).start();

        return view;
    }


    Runnable uploadStarCnt = new Runnable() {
        @Override
        public void run() {

            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/upload_star?"
                    + "user_id="
                    + user_id
                    + "&star_num="
                    + getStarCount();

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();

                JSONObject jsonObject = new JSONObject(docJSON.text());

                String verification = jsonObject.getString("status");

                if(verification.equals("true")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "upload stars",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }
    };


    protected void getUserId(){
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


    protected void buttonEvent(){
        exercise_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getActivity(), ReportExercise.class);
                startActivity(i1);
            }
        });

        stars_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i2 = new Intent(getActivity(), ReportStars.class);
                startActivity(i2);
            }
        });

        days_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(getActivity(), ReportDays.class);
                startActivity(i3);
            }
        });

        scores_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i4 = new Intent(getActivity(), ReportScores.class);
                startActivity(i4);
            }
        });
        self_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i5 = new Intent(getActivity(), HurtSelfReportActivity.class);
                startActivity(i5);
            }
        });
        counter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i6 = new Intent(getActivity(), TimerActivity.class);
                startActivity(i6);
            }
        });
    }


    protected void initView(View view){

        exercise_btn = view.findViewById(R.id.exercise_btn);
        exercise_unit = view.findViewById(R.id.exercise_unit);
        TextView exercise_count = view.findViewById(R.id.exercise_temp);
        exercise_count.setText(String.valueOf(getExerciseCount()));

        stars_btn = view.findViewById(R.id.stars_btn);
        TextView stars_count = view.findViewById(R.id.star_temp);
        stars_count.setText(String.valueOf(getStarCount()));


        days_btn = view.findViewById(R.id.day_btn);
        TextView days_count = view.findViewById(R.id.days_temp);
        days_count.setText(String.valueOf(getDayCount()));

        scores_btn = view.findViewById(R.id.score_btn);
        TextView scores_count = view.findViewById(R.id.scores_temp);
        scores_count.setText(String.valueOf(getHurtLevel()));

        self_report_btn = view.findViewById(R.id.self_report_btn);

        counter_btn = view.findViewById(R.id.couter_btn);
    }


    protected int getDayCount(){
        int day;

        try{
            realm = Realm.getDefaultInstance();
            day = realm.where(UserState.class)
                    .findAll()
                    .sum("day_count")
                    .intValue();
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }

        return day;
    }


    protected int getStarCount(){
        int star;

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


    protected int getExerciseCount(){

        try{
            realm = Realm.getDefaultInstance();
            int exercise_min = realm.where(UserState.class)
                    .findAll()
                    .sum("exercise_count")
                    .intValue();

            if(exercise_min < 120){
                exercise_unit.setText("分鐘");
                return exercise_min;
            }
            else{
                exercise_unit.setText("小時");
                return exercise_min/60;
            }
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }
    }


    protected int getHurtLevel(){
        int avg;

        realm = Realm.getDefaultInstance();
        RealmResults<UserState> hurt_level = realm.where(UserState.class).findAll();
        avg = (int) (hurt_level.average("current_hurt_count") + 0.5d);

        return avg;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void initRealm() {
        Realm.init(getActivity());
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
