package com.example.sylviameow.exercisealarm.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.myBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TipsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips, null);

        writeToTips(view);

        return view;
    }


    protected void writeToTips(View view){
        ListView tips_view_list;
        myBaseAdapter tips_adapter;
        List<Map> tips_string_list = new ArrayList<>();

        for(int i=0; i<5; i++){
            Map<String, String> map = new HashMap<String, String>();
            map.put(""+i, "體重管理");

            tips_string_list.add(map);
        }

        tips_view_list = view.findViewById(R.id.list_tips);

        tips_adapter = new myBaseAdapter(getActivity(), tips_string_list, 3);

        tips_view_list.setAdapter(tips_adapter);
    }
}
