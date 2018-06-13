package com.example.sylviameow.exercisealarm.Activity.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FillInfoActivity extends AppCompatActivity {

    de.hdodenhof.circleimageview.CircleImageView portrait;
    EditText phone;
    EditText username;
    EditText psw;
    EditText confirm_psw;
    Button submit_btm;

    SharedPreferences portrait_image;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        imageChoose();

        registerProcess();
    }


    protected void imageChoose()
    {
        portrait.setOnClickListener(new View.OnClickListener()
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
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                SharedPreferences.Editor editor = portrait_image.edit();
                editor.putString("uri", imageUri.toString());
                editor.apply();

                portrait.setImageBitmap(selectedImage);

                Toast.makeText(FillInfoActivity.this, "Picked an customised image", Toast.LENGTH_SHORT).show();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(FillInfoActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(FillInfoActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    protected void registerProcess(){

        submit_btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass = psw.getText().toString();
                String confirm_pass = confirm_psw.getText().toString();

                if(pass.equals(confirm_pass)){

                    /* Validate the psw */
                    new Thread(register).start();
                    /* Validation end */

                }
                else{
                    Toast.makeText(FillInfoActivity.this, "請輸入相同密碼", Toast.LENGTH_SHORT).show();
                    psw.setText("");
                    confirm_psw.setText("");
                    registerProcess();
                }

            }
        });
    }


    Runnable register = new Runnable() {
        @Override
        public void run() {

            String uname = phone.getText().toString();
            String u_nickname = username.getText().toString();
            String pass = psw.getText().toString();


            String url = "http://104.236.150.123:8080/ExerciseAlarmCMS/api/user/signup?user_username="
                    + uname
                    + "&user_password="
                    + pass
                    + "&user_nickname="
                    + u_nickname
                    + "&user_platform=android";

            try{
                Document doc = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .post();
                Element docJSON = Jsoup.parseBodyFragment(doc.html()).body();

                JSONObject jsonObject = new JSONObject(docJSON.text());

                if(jsonObject.getString("status").equals("true")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            confirmDialog();
                        }
                    });
                }
                else{
                    final String msg = jsonObject.getString("msg");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FillInfoActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                Log.e("MYAPP", "unexpected JSON exception", e);
            }
        }
    };


    protected void confirmDialog()
    {
            new AlertDialog.Builder(FillInfoActivity.this).setMessage("註冊成功！")
                .setPositiveButton("去登錄", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(FillInfoActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                })
                .show();
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
        portrait = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.portrait_image);
        phone = (EditText) findViewById(R.id.phone_num);
        username = (EditText) findViewById(R.id.user_name);
        psw = (EditText) findViewById(R.id.set_psw);
        confirm_psw = (EditText) findViewById(R.id.confirm_psw);
        submit_btm = (Button) findViewById(R.id.submit_btn);

        portrait_image = getApplicationContext().getSharedPreferences("portrait_uri", MODE_APPEND);
    }
}
