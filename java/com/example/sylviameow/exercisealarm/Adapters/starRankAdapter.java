package com.example.sylviameow.exercisealarm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class starRankAdapter extends BaseAdapter {
    private List<Map> data;
    private Context mContext;
    private LayoutInflater mInflater;        // 动态布局映射


    public starRankAdapter(Context mContext, List<Map> data) {

//        super();
        this.mContext = mContext;
        this.data = data;
        this.mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_star_rank, null);
        }

        TextView list_num = convertView.findViewById(R.id.list_num);
        TextView user_name = convertView.findViewById(R.id.user_name);
        TextView user_star = convertView.findViewById(R.id.user_star);

        String s = data.get(position).values().toString();
        s = s.substring(1,s.length()-1);
        String[] infos = s.split("-");
        list_num.setText(infos[0]);
        user_name.setText(infos[1]);
        String temp = infos[2] + " ";
        user_star.setText(temp);

        return convertView;

    }
}
