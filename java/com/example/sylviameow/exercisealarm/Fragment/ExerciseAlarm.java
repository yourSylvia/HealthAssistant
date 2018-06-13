package com.example.sylviameow.exercisealarm.Fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Activity.AlarmSetUp;
import com.example.sylviameow.exercisealarm.Activity.RingingActivity;
import com.example.sylviameow.exercisealarm.Database.AlarmInfo;
import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.myBaseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/* 直接获取整个shared preferences
 * 点击item并删除的时候 获取item在sp中的index */

/* 2018-03-28 闹钟列表存入数据库 */

public class ExerciseAlarm extends Fragment {
    private int pos;                   // position of clicked item

    List<Map> alarm_list;

    private SharedPreferences sp_alarm;

    private ListView alarm_view_list;
    private myBaseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_alarm, null);

        initView(view);

        initRealm();

        writeToList();

        itemClicked();

        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.new_alarm, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alarm_setting:
                Intent intent = new Intent(getActivity(), AlarmSetUp.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void writeToList() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);

        /* Get alarm list from shared preferences */
//        sp_alarm = getActivity().getSharedPreferences("alarm_clock", MODE_APPEND);
//        long state = sp_alarm.getLong("alarm_clock", 100);
//
//        if(state == 100){
//            int maxIndex = sp_alarm.getInt("maxIndex",0);
//            for(int i = 0; i < maxIndex; i++){
//                String temp = formatter.format(sp_alarm.getLong("alarm"+i, 100));
//                alarm_list.add(temp);
//            }
//
//            adapter = new myBaseAdapter(getActivity().getApplicationContext(), alarm_list, 1);
//            alarm_view_list.setAdapter(adapter);
//        }

        /* Get alarm list from DB */
        Realm realm = Realm.getDefaultInstance();

        RealmResults<AlarmInfo> infos = realm.where(AlarmInfo.class)
                .greaterThan("alarm_id", 0)
                .findAll();
        infos.load();

        for (int i = 0; i < infos.size(); i++) {
            Map<String, AlarmInfo> map = new HashMap<>();

            try {
                map.put("key" ,  infos.get(i));
            } catch (NullPointerException e) {
                Logger.getLogger("" + e);
            }

            alarm_list.add(map);
        }

        realm.close();

        adapter = new myBaseAdapter(getActivity().getApplicationContext(), alarm_list, 1);
        alarm_view_list.setAdapter(adapter);
    }


    protected void itemClicked() {
        alarm_view_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showItemDialog(view);

                pos = adapterView.getPositionForView(view);
                Toast.makeText(getActivity(), "position" + pos, Toast.LENGTH_SHORT).show();
            }
        });
    }


    protected void showItemDialog(View view) {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.mipmap.ic_launcher);

        final String[] Items = {"修改", "刪除", "取消"};
        builder.setItems(Items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Realm realm = Realm.getDefaultInstance();
                    String s = ((AlarmInfo)alarm_list.get(pos).get("key")).getTime();

                    final RealmResults<AlarmInfo> results = realm.where(AlarmInfo.class)
                            .equalTo("time", s)
                            .findAll();
                    long id = results.get(0).getAlarm_id();

                    Intent intent = new Intent(getContext(), AlarmSetUp.class);
                    intent.putExtra("update", 1);
                    intent.putExtra("id", id);
                    startActivity(intent);
                } else if (i == 1) {
                    /* Remove the item from DB*/
                    Realm realm = Realm.getDefaultInstance();
                    String s = ((AlarmInfo)alarm_list.get(pos).get("key")).getTime();

                    final RealmResults<AlarmInfo> results = realm.where(AlarmInfo.class)
                            .equalTo("time", s)
                            .findAll();
                    int id = results.get(0).getAlarm_id();
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getActivity(), RingingActivity.class);

                    alarmManager.cancel(PendingIntent.getActivity(getActivity(), id, intent, 0));

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            results.deleteAllFromRealm();
                        }
                    });

                    /* Remove the item from listview */
                    alarm_list.remove(pos);
                    adapter.notifyDataSetChanged();

                }
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void initView(View view) {
        alarm_list = new ArrayList<>();
        alarm_view_list = view.findViewById(R.id.alarm_list);
    }


    protected void initRealm() {
        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}

