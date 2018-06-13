package com.example.sylviameow.exercisealarm.Activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.Database.AlarmInfo;
import com.example.sylviameow.exercisealarm.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/* 判断是否update
 * 如果是
 * 则获取item在sp中的index并更新*/

public class AlarmSetUp extends AppCompatActivity
{
    private Button save_btn;
    private Button image_btn;
    private Button sound_btn;


    private SharedPreferences sp_image;

    private TimePicker timepicker;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    private long alarm_time;
    private int alarm_type =1;
    private int alarmId;


    private int update_state;
    private long position;

    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set_up);

        Intent intent = getIntent();
        update_state = intent.getIntExtra("update", 0);
        position = intent.getLongExtra("id", -1);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        initRealm();

        setAlarm();

        bindViews();

        imageChoose();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmDialog();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindViews()
    {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        save_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(update_state == 0){
                    /* Confirm alarm and ask the mode */
//                    alarmTypeModeDialog();
                    saveTimeToDB();
                }
                else{
                    updateTimeToDB();
                }

                Intent intent1 = new Intent(AlarmSetUp.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }


    protected void setAlarm()
    {
        alarm_time = Calendar.getInstance().getTimeInMillis();
        timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                /* Set current time */
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());

                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 1);
                /* Set alarm manager starts the intent at the corresponding calendar time */

                alarm_time = c.getTimeInMillis();
            }
        });
    }


    protected void confirmDialog()
    {
        new AlertDialog.Builder(AlarmSetUp.this).setMessage("確認不保存更改繼續返回?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //SimpleDateFormat formatter = new SimpleDateFormat("hh:mm", Locale.CHINA);
                        Intent intent = new Intent(AlarmSetUp.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


//    protected void alarmTypeModeDialog(){
//        new AlertDialog.Builder(AlarmSetUp.this).setMessage("設定鬧鐘重複提醒？")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        alarm_type = 1;
//                        saveTimeToDB();
//
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        alarm_type = 0;
//                        saveTimeToDB();
//                    }
//                })
//                .show();
//    }


    protected void saveTimeToDB(){
        /* Save time to DB */
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                AlarmInfo alarmInfo = realm.createObject(AlarmInfo.class, alarmId);
                alarmInfo.setTime_stamp(alarm_time);
                alarmInfo.setTime(formatter.format(alarm_time));
                alarmInfo.setAlarm_type(alarm_type);
            }
        });


        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, pi);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2000, pi);
        Toast.makeText(AlarmSetUp.this, "設置成功", Toast.LENGTH_SHORT).show();
        realm.close();
    }


    protected void updateTimeToDB(){
         /* Modify the item at the corresponding position */
        final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AlarmInfo results = realm.where(AlarmInfo.class)
                        .equalTo("alarm_id", position)
                        .findFirst();

                results.setTime_stamp(alarm_time);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time-1000*60, pi);
                results.setTime(formatter.format(alarm_time));
            }
        });

        Toast.makeText(AlarmSetUp.this, "修改成功", Toast.LENGTH_SHORT).show();

        realm.close();
    }


    protected void imageChoose()
    {
        image_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // save to shared preference
                SharedPreferences.Editor editor = sp_image.edit();
                editor.putString("img_uri_"+alarmId, imageUri.toString());
                editor.putString("img_uri_"+alarmId, imageUri.toString());
                editor.apply();

                // save to DB

                Toast.makeText(AlarmSetUp.this, "Picked an customised image", Toast.LENGTH_SHORT).show();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AlarmSetUp.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(AlarmSetUp.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("WrongConstant")
    private void initView()
    {
        save_btn = (Button) findViewById(R.id.save_btn);
        image_btn = (Button) findViewById(R.id.image_btn);
        sound_btn = (Button) findViewById(R.id.sound_btn);
        sound_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AlarmSetUp.this, RingSelectionActivity.class);
                i.putExtra("alarmId", alarmId);
                startActivity(i);
            }
        });

        sp_image = getApplicationContext().getSharedPreferences("alarm_setting", Context.MODE_APPEND);

        timepicker = (TimePicker) findViewById(R.id.time_picker);
        timepicker.setIs24HourView(true);                           // Set 24 hours format
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number maxID = realm.where(AlarmInfo.class).max("alarm_id");
                int nextID = (maxID == null) ? 1:maxID.intValue() + 1;
                alarmId = nextID;
                final Intent intent = new Intent(AlarmSetUp.this, RingingActivity.class);
                intent.putExtra("alarmId", alarmId);
                pi = PendingIntent.getActivity(getApplicationContext(),alarmId, intent,0);

            }
        });
    }


    protected void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new  RealmConfiguration.Builder()
                .name("myDB.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
