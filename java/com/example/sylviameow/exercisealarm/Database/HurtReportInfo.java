package com.example.sylviameow.exercisealarm.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class HurtReportInfo extends RealmObject {

    @PrimaryKey
    private long id;

    @Required
    private String date;
    private String timestamp;
    private int hurt_level;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public int getHurt_level(){
        return hurt_level;
    }

    public void setHurt_level(int hurt_level){
        this.hurt_level = hurt_level;
    }

    public String getCurrentDate() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
    }
}
