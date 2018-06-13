package com.example.sylviameow.exercisealarm.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class RingSelectionActivity extends AppCompatActivity {


    private LinearLayout btnRingtoneFromSDCard;
    private LinearLayout btnRecord;
    private SharedPreferences sp_alarm_setting;
    private int alarmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_selection);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        bindView();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri ringtoneUri = data.getData();
                if(ringtoneUri == null) throw new FileNotFoundException();

                final InputStream imageStream = getContentResolver().openInputStream(ringtoneUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                SharedPreferences.Editor editor = sp_alarm_setting.edit();
                editor.putString("ringtone_uri_"+alarmId, ringtoneUri.toString());
                editor.apply();

                Toast.makeText(RingSelectionActivity.this, "Picked an customised ringtone", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(RingSelectionActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(RingSelectionActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {

        btnRingtoneFromSDCard = findViewById(R.id.normal_answer_btn);
        btnRecord= findViewById(R.id.my_answer_btn);

        sp_alarm_setting = getApplicationContext().getSharedPreferences("alarm_setting", Context.MODE_APPEND);

        alarmId = getIntent().getIntExtra("alarmId",0);

    }

    private void bindView() {


        btnRingtoneFromSDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ringtonePickerIntent = new Intent(Intent.ACTION_PICK);
                ringtonePickerIntent.setType("audio/*");
                ringtonePickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(ringtonePickerIntent,"Select Audio "),  1);

            }


        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RingSelectionActivity.this,RecorderActivity.class);
                intent.putExtra("alarmId", alarmId);
                startActivity(intent);
            }
        });
    }


}
