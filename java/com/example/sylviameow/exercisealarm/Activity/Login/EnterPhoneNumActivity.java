package com.example.sylviameow.exercisealarm.Activity.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class EnterPhoneNumActivity extends AppCompatActivity {

    EditText phone_num;
    Button next_btn;
    private String verify_code;
    private String phone;
    private String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_num);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        phone_num = (EditText) findViewById(R.id.phone_num);

        next_btn = (Button) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phone_num.getText().toString();
                confirmDialog(phone);
            }
        });
    }


    protected void confirmDialog(String phone)
    {
        new AlertDialog.Builder(EnterPhoneNumActivity.this).setMessage("即將發送驗證碼到以下手機\n" + phone )
                .setPositiveButton("確認", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        /* Send verification code to phone */
                        new Thread(sendVerificationCode).start();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    Runnable sendVerificationCode = new Runnable() {
        @Override
        public void run() {

            phone = phone_num.getText().toString();
            region = "%2b852";

            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/verify?user_username="
                    +region
                    +phone
                    +"&user_platform=android";

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();
                JSONObject jsonObject = new JSONObject(docJSON.text());

                JSONObject temp = jsonObject.getJSONObject("data");
                verify_code = temp.getString("verify_code");

                Intent intent = new Intent(EnterPhoneNumActivity.this, VerificationActivity.class);
                intent.putExtra("verify_code", verify_code);
                intent.putExtra("u_name", region+phone);
                startActivity(intent);

            }

            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }
    };


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
