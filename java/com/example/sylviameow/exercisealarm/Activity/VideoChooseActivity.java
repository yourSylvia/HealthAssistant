package com.example.sylviameow.exercisealarm.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.VideoListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoChooseActivity extends AppCompatActivity {

    ListView video_view_list;
    List<Map> video_string_list;

    VideoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_choose);

        initView();
        writeToVideoList();
    }


    protected void writeToVideoList() {
        video_string_list = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            Map<String, String> map = new HashMap<>();
            map.put(""+i, "視頻 " + i);

            video_string_list.add(map);
        }

        adapter = new VideoListAdapter(VideoChooseActivity.this, video_string_list, 1);
        video_view_list.setAdapter(adapter);
    }


    protected void initView(){
        video_view_list = (ListView) findViewById(R.id.list_video);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.video_selected:
                ArrayList<String> arrayList = adapter.getCheckBoxIDList();
                ArrayList<String> video_name_list = new ArrayList<>();

                for(int i=0; i<arrayList.size(); i++){
                    for(int j=0; j<video_string_list.size(); j++){
                        String set = video_string_list.get(j).keySet().toString();
                        set = set.substring(1, set.length()-1);

                        if(arrayList.get(i).equals(set)){
                            video_name_list.add(video_string_list.get(j).get(set).toString());
                        }
                    }
                }

                // Save to shared preference
                SharedPreferences.Editor editor = getSharedPreferences("videoList", MODE_PRIVATE).edit();

                editor.putInt("VideoNums", video_name_list.size());

                for (int i = 0; i < video_name_list.size(); i++) {
                    editor.putString("video_"+i, video_name_list.get(i));
                }
                editor.apply();

                // Back
                Intent i = new Intent(VideoChooseActivity.this, MainActivity.class);
                startActivity(i);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_select, menu);
        return true;
    }
}
