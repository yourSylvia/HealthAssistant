package com.example.sylviameow.exercisealarm.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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


public class MonthFragment extends BaseFragment {

    private LineChartView lineChar;
    private TextView tv_normal;

    private ArrayList<String> XDATA;
    //图表的数据点
    private ArrayList<Integer> POINT;

    public static MonthFragment newInstance() {
        MonthFragment fragment = new MonthFragment();
        return fragment;
    }


    private Map<List<String>, List<Integer>> getMonthData(){
        Realm realm = null;
        HashMap<List<String>, List<Integer>> map = new HashMap<>();

        try{
            realm = Realm.getDefaultInstance();

            String currMonth = getCurrentDate().substring(4,9);
            RealmResults<UserState> results = realm
                    .where(UserState.class)
                    .contains("date", currMonth)
                    .findAll();

            if(results.size() == 0){
                tv_normal.setVisibility(View.VISIBLE);
                lineChar.setVisibility(View.GONE);
                map = null;
            }
            else{
                XDATA = new ArrayList<>();
                POINT = new ArrayList<>();

                for(int i=0; i<results.size(); i++){
                    XDATA.add(results.get(i).getDate().substring(5));
                    POINT.add(results.get(i).getCurrent_hurt_count());
                }

                map.put(XDATA, POINT);
            }
        }
        finally {
            if(realm != null){
                realm.close();
            }
        }
        return map;
    }


    private String getCurrentDate() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
    }


    @Override
    protected int getResId() {
        return R.layout.linechart;
    }

    @Override
    protected void initView(View view) {
        lineChar = view.findViewById(R.id.lineChart);
        tv_normal = view.findViewById(R.id.tv_normal);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Map<List<String>, List<Integer>> dailyData = getMonthData();
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
