package com.example.sylviameow.exercisealarm.Fragment;

import android.os.Bundle;
import android.view.View;

import com.example.sylviameow.exercisealarm.Database.HurtReportInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import lecho.lib.hellocharts.view.LineChartView;


public class WeekFragment extends BaseFragment {

    private LineChartView lineChar;
    private ArrayList<String> XDATA;
    //图表的数据点
    private ArrayList<Integer> POINT;

    public static WeekFragment newInstance() {
        WeekFragment fragment = new WeekFragment();
        return fragment;
    }


    private Map<List<String>, List<Integer>> getWeekData(){
        Realm realm = null;
        HashMap<List<String>, List<Integer>> map = new HashMap<>();

        try{
            realm = Realm.getDefaultInstance();
            XDATA = new ArrayList<>();
            POINT = new ArrayList<>();

            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
            SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd", Locale.CHINA);
            // current week days
            for (int i = 6; i >= 0; i--) {
                Calendar currentDate = Calendar.getInstance();
                currentDate.add(Calendar.DAY_OF_MONTH, -i);
                String format_date = formatter1.format(currentDate.getTime());

                RealmResults<UserState> results = realm
                        .where(UserState.class)
                        .equalTo("date", format_date)
                        .findAll();

                if(results.size() == 0){
                    XDATA.add(formatter2.format(currentDate.getTime()));
                    POINT.add(0);
                }
                else{
                    XDATA.add(formatter2.format(currentDate.getTime()));
                    POINT.add(results.get(0).getCurrent_hurt_count());
                }
            }
            map.put(XDATA, POINT);
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }
        return map;
    }


    @Override
    protected int getResId() {
        return R.layout.linechart;
    }

    @Override
    protected void initView(View view) {
        lineChar = view.findViewById(R.id.lineChart);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Map<List<String>, List<Integer>> dailyData = getWeekData();
        if (dailyData != null) {
            Set<List<String>> lists = dailyData.keySet();
            Iterator<List<String>> iterator = lists.iterator();
            if (iterator.hasNext()) {
                ArrayList<String> next = (ArrayList<String>) iterator.next();
                ArrayList<Integer> integers = (ArrayList<Integer>) dailyData.get(next);

                getAxisXLables(next);
                getAxisPoints(integers);
            }

            initLineChart(lineChar,"#696969", 10);
        }
    }

    protected void initRealm() {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
