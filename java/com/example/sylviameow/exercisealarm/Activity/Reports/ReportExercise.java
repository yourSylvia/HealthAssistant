package com.example.sylviameow.exercisealarm.Activity.Reports;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Database.AlarmInfo;
import com.example.sylviameow.exercisealarm.Database.UserState;
import com.example.sylviameow.exercisealarm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import lecho.lib.hellocharts.formatter.ColumnChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleColumnChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ReportExercise extends AppCompatActivity {

    private ColumnChartView columnChartView;
    private List<String> dates;
    private TextView minute;
    private TextView exercise_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_exercise);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        chartReportData();
    }


    protected void chartReportData(){
        // 最近七天的数据
        List<Integer> testData = new ArrayList<>();

        Realm realm = null;
        try{
            realm = Realm.getDefaultInstance();
            RealmResults<UserState> results = realm.where(UserState.class)
                    .findAll()
                    .sort("id", Sort.DESCENDING);

            int size = results.size();
            if(size < 7){
                while(7 - size > 0){
                    testData.add(0);
                    size ++;
                }
                for(int i = 0; i < results.size(); i++){
                    testData.add(results.get(i).getExercise_count());
                }
            }
            else{
                for (int i = 0; i < 7; i++) {
                    testData.add(results.get(i).getExercise_count());
                }
            }

        }finally {
           if(realm != null){
               realm.close();
           }
        }

        setChartFeature(testData, Color.parseColor("#4D008B8B"), columnChartView);
    }


    protected void setChartFeature(List<Integer> columnDatas, int columnColor, ColumnChartView charView){

        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd", Locale.CHINA);

        /* Recent week */
        for (int i = 0; i < 7; i++) {
            Calendar currentDate = Calendar.getInstance();
            currentDate.add(Calendar.DAY_OF_MONTH, -i);
            String format_date = format1.format(currentDate.getTime());
            dates.add(format_date);
        }

        // 使用的 7列，每列1个subcolumn。
        int numSubcolumns = 1;
        int numColumns = 7;
        //定义一个圆柱对象集合
        List<Column> columns = new ArrayList<Column>();
        //子列数据集合
        List<SubcolumnValue> values;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        //遍历列数numColumns
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            //遍历每一列的每一个子列
            for (int j = 0; j < numSubcolumns; ++j) {
                //为每一柱图添加颜色和数值
                float f = columnDatas.get(i);
                values.add(new SubcolumnValue(f, columnColor));
            }

            //创建Column对象
            Column column = new Column(values);
            ColumnChartValueFormatter chartValueFormatter = new SimpleColumnChartValueFormatter(0);
            column.setFormatter(chartValueFormatter);
            //是否有数据标注
            column.setHasLabels(true);
            //是否是点击圆柱才显示数据标注
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
            //给x轴坐标设置描述
            axisValues.add(new AxisValue(i).setLabel(dates.get(numColumns - i - 1)));
        }

        //创建一个带有之前圆柱对象column集合的ColumnChartData
        ColumnChartData columnChartData = new ColumnChartData(columns);

        columnChartData.setValueLabelTextSize(8);
        columnChartData.setValueLabelBackgroundColor(Color.parseColor("#00000000"));
        //data.setValueLabelTypeface(Typeface.DEFAULT);
        //设置数据文字样式
        columnChartData.setValueLabelBackgroundEnabled(true);
        columnChartData.setValueLabelBackgroundAuto(false);

        //定义x轴y轴相应参数
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("時間（分鐘）");//轴名称
        axisY.hasLines();//是否显示网格线
        axisY.setTextColor(Color.parseColor("#696969"));//颜色
        axisX.hasLines();
        axisX.setTextColor(Color.parseColor("#696969"));
        axisX.setValues(axisValues);
        axisX.setTextSize(10);
        axisX.setHasSeparationLine(false);

        //把X轴Y轴数据设置到ColumnChartData 对象中
        columnChartData.setAxisXBottom(axisX);
        columnChartData.setAxisYLeft(axisY);
        //给表填充数据，显示出来
        charView.setInteractive(false);
        charView.setColumnChartData(columnChartData);
    }


    protected int getExerciseCount(){
        Realm realm = null;

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
        minute = (TextView) findViewById(R.id.t2);
        exercise_unit = (TextView) findViewById(R.id.t3);
        minute.setText(String.valueOf(getExerciseCount()));

        dates = new ArrayList<>();
        columnChartView = (ColumnChartView) findViewById(R.id.exercise_chart);
    }


    protected void initRealm() {
        Realm.init(ReportExercise.this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
