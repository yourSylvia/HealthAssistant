package com.example.sylviameow.exercisealarm.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Activity.RingingActivity;
import com.example.sylviameow.exercisealarm.Database.AlarmInfo;
import com.example.sylviameow.exercisealarm.Database.UserServerInfo;
import com.example.sylviameow.exercisealarm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class VideoListAdapter extends BaseAdapter {
    private int type;                        // 使用类型
    private List<Map> data;
    private Context mContext;
    private LayoutInflater mInflater;        // 动态布局映射

    private ArrayList<String> checkBoxIDList;

    public VideoListAdapter(Context mContext, List<Map> data, int type) {

//        super();
        this.mContext = mContext;
        this.data = data;
        this.mInflater = LayoutInflater.from(mContext);
        this.type = type;

        checkBoxIDList= new ArrayList<>();
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object getItem(int position) {

        if (type == 0 || type == 1) {
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

        switch (type){
            case 0:
                return presentVideo(position, convertView, parent);
            case 1:
                return presentVideoChooseList(position, convertView, parent);
        }

        return presentVideo(position, convertView, parent);

    }


    private View presentVideo(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_video_list, null);
        }

        TextView textView1 = convertView.findViewById(R.id.video_name);
        String s = data.get(position).values().toString();
        textView1.setText(s.substring(1, s.length()-1));

        return convertView;
    }


    private View presentVideoChooseList(final int position, View convertView, ViewGroup parent){
        final TestViewHolder testViewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_video_choose_list, null);
            testViewHolder = new TestViewHolder();
            testViewHolder.item_checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(testViewHolder);
        }
        else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }

        TextView textView1 = convertView.findViewById(R.id.video_name);
        String s = data.get(position).values().toString();
        textView1.setText(s.substring(1, s.length()-1));

        testViewHolder.item_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    // Record the id of the video
                    String s = data.get(position).keySet().toString();
                    checkBoxIDList.add(s.substring(1, s.length()-1));
                }
                else{
                    String s = data.get(position).keySet().toString();
                    checkBoxIDList.remove(s.substring(1, s.length()-1));
                }
            }
        });

        return convertView;
    }


    public ArrayList<String> getCheckBoxIDList() {
        return checkBoxIDList;
    }


    public void setCheckBoxIDList(ArrayList<String> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }


    static class TestViewHolder {
        CheckBox item_checkBox;
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
