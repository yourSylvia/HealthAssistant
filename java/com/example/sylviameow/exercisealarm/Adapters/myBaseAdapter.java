package com.example.sylviameow.exercisealarm.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Activity.AlarmSetUp;
import com.example.sylviameow.exercisealarm.Activity.RingingActivity;
import com.example.sylviameow.exercisealarm.Database.AlarmInfo;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.R;
import com.suke.widget.SwitchButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.annotations.Index;

/* Disable 相应时间的 alarm manager */

public class myBaseAdapter extends BaseAdapter {
    private int type;                        // 使用类型
    private List<Map> data;
    private Context mContext;
    private LayoutInflater mInflater;        // 动态布局映射

    private String nickname;

    public myBaseAdapter(Context mContext, List<Map> data, int type) {

//        super();
        this.mContext = mContext;
        this.data = data;
        this.mInflater = LayoutInflater.from(mContext);
        this.type = type;
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object getItem(int position) {

        if (type == 4 || type == 5) {
            String id = data.get(position).keySet().toString();

            return id.substring(1, id.length() - 1);
        }

        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (type) {

            // Alarm list
            case 1:
                return presentAlarm(position, convertView, parent);

            // Video list
//            case 2:
//                return presentVideo(position, convertView, parent);

            // Tip list
            case 3:
                return presentTips(position, convertView, parent);

            // Normal question list
            case 4:
                return presentNormalQuestions(position, convertView, parent);

            // My question list
            case 5:
                return presentMyQuestions(position, convertView, parent);

            // My reply list
            case 6:
                return presentComment(position, convertView, parent);

            // Star ranking
            case 7:
                return starRanking(position, convertView, parent);

            default:
                return null;

        }

    }


    private View presentAlarm(int position, View convertView, ViewGroup parent) {
        initRealm();

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_alarm_list, null);
        }

        TextView textView = convertView.findViewById(R.id.time);
        AlarmInfo alarmInfo = (AlarmInfo) data.get(position).get("key");
        final String alarmInfoTime = alarmInfo.getTime();
        final long alarmMillisecond = alarmInfo.getTime_stamp();
        final int alarmId = alarmInfo.getAlarm_id();

        textView.setText(alarmInfoTime);

        SwitchButton switchButton = convertView.findViewById(R.id.switch_button);
        switchButton.setChecked(alarmInfo.getAlarm_type() == 1);
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                Realm realm = null;

                /* Disable the alarm manager broadcast */
                if (isChecked) {
                    long timeUp = alarmMillisecond;
                    while (timeUp < System.currentTimeMillis()) {
                        timeUp = alarmMillisecond + 1000 * 3600 * 24;
                    }

                    final Intent intent = new Intent(mContext, RingingActivity.class);
                    intent.putExtra("alarmId", alarmId);
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pi = PendingIntent.getActivity(mContext, alarmId, intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeUp, pi);

                    try {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                AlarmInfo results = realm.where(AlarmInfo.class)
                                        .equalTo("alarm_id", alarmId)
                                        .findFirst();

                                results.setAlarm_type(1);
                            }
                        });

                    } finally {
                        if (realm != null) {
                            realm.close();
                        }
                    }

                } else {
                    AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(mContext, RingingActivity.class);

                    alarmManager.cancel(PendingIntent.getActivity(mContext, alarmId, intent, 0));

                    try {
                        realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                AlarmInfo results = realm.where(AlarmInfo.class)
                                        .equalTo("alarm_id", alarmId)
                                        .findFirst();

                                results.setAlarm_type(0);

                            }
                        });

                    } finally {
                        if (realm != null) {
                            realm.close();
                        }
                    }
                }

            }
        });

        return convertView;

    }


//    private View presentVideo(int position, View convertView, ViewGroup parent) {
//
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.item_video_list, null);
//        }
//
//        TextView textView1 = convertView.findViewById(R.id.video_name);
//        textView1.setText("運動");
//
//        return convertView;
//    }


    private View presentTips(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_tips_list, null);
        }

        TextView textView1 = convertView.findViewById(R.id.tips_name);
        TextView textView2 = convertView.findViewById(R.id.tips_content);

        String title = data.get(position).values().toString();
        textView1.setText(title.substring(1, title.length() - 1));
        textView2.setText("這是一個簡單說明");

        return convertView;
    }


    private View presentNormalQuestions(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_question_list, null);
        }

        TextView ques_title = convertView.findViewById(R.id.ques_title);
        String title = data.get(position).values().toString();
        ques_title.setText(title.substring(1, title.length() - 1));

        return convertView;
    }


    private View presentMyQuestions(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_question_list, null);
        }

        TextView ques_title = convertView.findViewById(R.id.ques_title);
        String title = data.get(position).values().toString();
        ques_title.setText(title.substring(1, title.length() - 1));

        return convertView;
    }


    private View presentComment(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_user_comment, null);
        }

        getUserInfoFromDB();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);

        TextView user_nickname = convertView.findViewById(R.id.user_nickname);
        TextView user_comment = convertView.findViewById(R.id.comment);
        TextView user_date = convertView.findViewById(R.id.create_date);

//        String nickname = data.get(position).keySet().toString();
//        user_nickname.setText(nickname.substring(1, nickname.length() - 1));

        String temp = data.get(position).values().toString();

        String[] comment_date = temp.substring(1,temp.length()-1).split("-");

        // user nickname
        if(comment_date[0].equals(nickname)){
            user_nickname.setText("我：");
        }
        else{
            String s = comment_date[0] + ": ";
            user_nickname.setText(s);
        }

        // comment
        user_comment.setText(comment_date[1]);

        // date
        Date date = new Date(Integer.parseInt(comment_date[2]));
        user_date.setText(format1.format(date));

        return convertView;
    }


    private View starRanking(int position, View convertView, ViewGroup parent){

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_user_comment, null);
        }



        return convertView;
    }


    private void getUserInfoFromDB(){
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

                    nickname = results.get(0).getUser_nickname();
                }
            });
        }
        finally {
            if (realm != null) {
                realm.close();
            }
        }
    }


    protected void initRealm() {
        Realm.init(mContext);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

}
