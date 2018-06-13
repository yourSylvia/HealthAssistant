package com.example.sylviameow.exercisealarm.Activity.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sylviameow.exercisealarm.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VerificationActivity extends AppCompatActivity {

    private com.chaos.view.PinView verify_code_input;
    private Button next_btn;
    private String code;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        /* Add back button on the top left */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();

        nextStepButtonEvent();
    }


    protected void nextStepButtonEvent(){
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(codeVerification(username, code)){
                    Intent intent = new Intent(VerificationActivity.this, FillInfoActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(VerificationActivity.this, "請輸入正確驗證碼", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    protected boolean codeVerification(String username, String code){
        String user_input = verify_code_input.getText().toString();
        String confusion = "android" + "!mM$*9" + username.replace("%2b", "+") + "Rd#s&D2" + user_input;
        String s;

        try {

            byte[] hash = MessageDigest.getInstance("MD5").digest(confusion.getBytes());
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }

            s = hex.toString();

            return s.equals(code);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return false;
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

        Toast.makeText(VerificationActivity.this, "驗證碼已發送", Toast.LENGTH_SHORT).show();

        verify_code_input = (com.chaos.view.PinView) findViewById(R.id.verfify_code);
        next_btn = (Button) findViewById(R.id.next_btn);

        /* Get verify code */
        Intent i = getIntent();
        code = i.getStringExtra("verify_code");
        username = i.getStringExtra("u_name");
    }
}
