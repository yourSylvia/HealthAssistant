package com.example.sylviameow.exercisealarm.Activity.Reports;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Adapters.MyPagerAdapter;
import com.example.sylviameow.exercisealarm.Adapters.ReportFragmentAdapter;
import com.example.sylviameow.exercisealarm.Database.HurtReportInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.Fragment.CurrDayFragment;
import com.example.sylviameow.exercisealarm.Fragment.MonthFragment;
import com.example.sylviameow.exercisealarm.Fragment.WeekFragment;
import com.example.sylviameow.exercisealarm.R;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ReportScores extends AppCompatActivity {

    private TextView score_txt1;
    private TextView score_txt2;
    private ImageView avg_img;
    private ImageView current_img;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_scores);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initRealm();
        initView();
        initData();
        setTextAndImage();
    }


    protected void setTextAndImage() {
        String avg = String.valueOf(computeAverage());
        String current = getDailyLevel();

        score_txt1.setText(avg);
        if(current.equals("N/A")){
            score_txt2.setText(current);
            score_txt2.setTextSize(25);
        }
        else{
            score_txt2.setText(current);
        }

        switch (avg) {
            case "0":
                avg_img.setImageResource(R.drawable.hurt01);
                break;
            case "1":
            case "2":
                avg_img.setImageResource(R.drawable.hurt02);
                break;
            case "3":
            case "4":
                avg_img.setImageResource(R.drawable.hurt03);
                break;
            case "5":
            case "6":
                avg_img.setImageResource(R.drawable.hurt04);
                break;
            case "7":
            case "8":
                avg_img.setImageResource(R.drawable.hurt05);
                break;
            case "9":
            case "10":
                avg_img.setImageResource(R.drawable.hurt06);
                break;
            default:
                avg_img.setImageResource(R.drawable.hurt01);
                break;

        }

        if (current.equals("N/A") || avg.equals("0")) {
            current_img.setImageResource(R.drawable.hurt_stable);
        } else if (Integer.parseInt(avg) < Integer.parseInt(current)) {
            current_img.setImageResource(R.drawable.hurt_up);
        } else if (Integer.parseInt(avg) < Integer.parseInt(current)) {
            current_img.setImageResource(R.drawable.hurt_down);
        } else {
            current_img.setImageResource(R.drawable.hurt_stable);
        }
    }


    protected int computeAverage() {
        int avg = 0;

        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserState> hurt_level = realm.where(UserState.class).findAll();
        avg = (int) (hurt_level.average("current_hurt_count") + 0.5d);


        return avg;
    }


    protected String getDailyLevel() {
        int hurt_id;
        String daily_level;

        Realm realm = Realm.getDefaultInstance();
        try {
            hurt_id = realm.where(HurtReportInfo.class)
                    .equalTo("date", getCurrentDate())
                    .findAll()
                    .max("id")
                    .intValue();

            int hurt_level = realm.where(HurtReportInfo.class)
                    .equalTo("id", hurt_id)
                    .findFirst()
                    .getHurt_level();

            daily_level = String.valueOf(hurt_level);
        } catch (NullPointerException e) {
            daily_level = "N/A";
        }

        return daily_level;
    }


    public String getCurrentDate() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
    }


    public long generateID(){
        long id;
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);

        Calendar c = Calendar.getInstance();
        id = Long.valueOf(formater.format(c.getTime()));

        return id;
    }


    protected void initView() {
        score_txt1 = (TextView) findViewById(R.id.avg_score_txt);
        score_txt2 = (TextView) findViewById(R.id.current_score_txt);

        avg_img = (ImageView) findViewById(R.id.avg_hurt_img);
        current_img = (ImageView) findViewById(R.id.current_hurt_img);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }


    private void initData(){
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(CurrDayFragment.newInstance());
        fragments.add(WeekFragment.newInstance());
        fragments.add(MonthFragment.newInstance());

        List<String> titles = new ArrayList<>();
        titles.add("當日");
        titles.add("7日");
        titles.add("當月");

        viewPager.setAdapter(new ReportFragmentAdapter(
                getSupportFragmentManager(),
                fragments,
                titles));
        tabLayout.setupWithViewPager(viewPager);
    }


    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
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
