package com.example.sylviameow.exercisealarm.Activity.Forum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.myBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalQuestionActivity extends AppCompatActivity {

    private ListView ques_title_view_list;
    private List<Map> ques_title_string_list;
    private int pos;

    private myBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_question);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        new Thread(networkTask).start();

    }


    protected void writeToQuestion(){

        ques_title_view_list = (ListView) findViewById(R.id.normal_question_list);

        adapter = new myBaseAdapter(NormalQuestionActivity.this, ques_title_string_list, 4);

        ques_title_view_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ques_title_view_list.requestFocusFromTouch();

        itemClicked();
    }


    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            ques_title_string_list = new ArrayList<>();

            try {
                Document doc = Jsoup.connect("http://104.236.150.123:8080/ExerciseAlarmCMS/api/discuss/select/all?discuss_offset=0&discuss_page_size=20")
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();

                JSONObject jsonObject = new JSONObject(docJSON.text());

                JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));

                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String title = jsonArray.getJSONObject(i).getString("title");
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(id, title);
                    ques_title_string_list.add(map);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        writeToQuestion();
                    }
                });

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }

    };


    protected void itemClicked(){

        ques_title_view_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                pos = adapterView.getPositionForView(view);
                adapter = new myBaseAdapter(NormalQuestionActivity.this, ques_title_string_list, 5);
                String id = adapter.getItem(pos).toString();
                Toast.makeText(NormalQuestionActivity.this, "Index" + id, Toast.LENGTH_SHORT).show();

                // Runnable

                Intent intent = new Intent(NormalQuestionActivity.this, MyQuesDetailsActivity.class);
                intent.putExtra("Ques_id",Integer.parseInt(id));
                startActivity(intent);

            }
        });

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
