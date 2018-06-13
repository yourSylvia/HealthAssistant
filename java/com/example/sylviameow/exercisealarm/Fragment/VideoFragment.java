package com.example.sylviameow.exercisealarm.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sylviameow.exercisealarm.Activity.ExplainationActivity;
import com.example.sylviameow.exercisealarm.Activity.VideoChooseActivity;
import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.VideoListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;

public class VideoFragment extends Fragment {

    private ListView video_view_list;

    ArrayList<String> selected_video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_video, null);
        setHasOptionsMenu(true);

        video_view_list = view.findViewById(R.id.list_video);
        selected_video = new ArrayList<>();
        Realm realm = null;

        // Get selected video list from sharedPreferences
        try{
            SharedPreferences sharedPreferences =
                    getContext().getSharedPreferences("videoList", MODE_PRIVATE);

            int environNums = sharedPreferences.getInt("VideoNums", 0);

            for (int i = 0; i < environNums; i++) {
                String environItem = sharedPreferences.getString("video_"+i, null);
                selected_video.add(environItem);
            }

            writeToVideoList(view);
            itemClicked();
        }
        catch (NullPointerException e){
            selected_video = null;
        }

        return view;
    }


    protected void writeToVideoList(View view) {
        VideoListAdapter adapter;
        List<Map> video_string_list = new ArrayList<>();
        video_view_list = view.findViewById(R.id.list_video);

        for(int i = 0; i < selected_video.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put(""+i, selected_video.get(i));

            video_string_list.add(map);
        }

        adapter = new VideoListAdapter(getContext(), video_string_list, 0);
        video_view_list.setAdapter(adapter);
    }


    protected void itemClicked(){
        video_view_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /* Pass video id to new activity */
                Intent intent = new Intent(getActivity(), ExplainationActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.video_selected:
                Intent i = new Intent(getContext(), VideoChooseActivity.class);
                startActivity(i);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.video_select, menu);
    }
}
