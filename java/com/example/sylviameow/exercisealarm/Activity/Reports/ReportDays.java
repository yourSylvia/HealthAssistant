package com.example.sylviameow.exercisealarm.Activity.Reports;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.AndroidException;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Database.HurtReportInfo;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.intellij.lang.annotations.Language;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ReportDays extends AppCompatActivity {

    private TextView days;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_days);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        initRealm();
        getActiveDays();
    }

    protected HashSet<CalendarDay> getActiveDays(){
        HashSet<CalendarDay> result = new HashSet<>();
        Realm realm = Realm.getDefaultInstance();
        try{
            RealmResults<UserState> userStateResult = realm.where(UserState.class)
                    .equalTo("day_count",1)
                    .findAll();
            for (int i = 0; i<userStateResult.size(); i++) {
                UserState state = userStateResult.get(i);
                String date = state.getId()+"";
                result.add(new CalendarDay(new SimpleDateFormat("yyyyMMdd").parse(date)));
            }
        }
        catch(NullPointerException e){
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
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


    protected void initRealm()
    {
        Realm.init(getApplicationContext());
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }


    protected void initView(){
        days = (TextView) findViewById(R.id.day_txt);
        days.setText(String.valueOf(getDayCount()));

        com.prolificinteractive.materialcalendarview.MaterialCalendarView
                calendarView = (com.prolificinteractive.materialcalendarview.MaterialCalendarView )
                findViewById(R.id.calendarView);
        calendarView.setSelectedDate(Calendar.getInstance());
        HashSet<CalendarDay> days = getActiveDays();
        calendarView.addDecorator(new EventDecorator(days));
    }


    protected int getDayCount(){
        int day;
        Realm realm = null;

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


    public class EventDecorator implements DayViewDecorator {

//        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(HashSet<CalendarDay>  dates) {
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
//            view.addSpan(new BackgroundColorSpan(R.color.Coral));
//            view.addSpan(new UnderlineSpan());
            view.addSpan(new RelativeSizeSpan(1.5f));
            view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.Coral)));
        }


    }
}
