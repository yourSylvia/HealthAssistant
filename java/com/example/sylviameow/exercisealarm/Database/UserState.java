package com.example.sylviameow.exercisealarm.Database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserState extends RealmObject {

    @PrimaryKey
    private long id;

    @Index
    private String user_id;

    @Required
    private String date;
    private int exercise_count;
    private int star_count;
    private int day_count;
    private int current_hurt_count;

    private RealmList<UserServerInfo> serverInfos;

    public long getId(){
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getUser_id(){
        return user_id;
    }

    public void setUser_id(String user_id){
        this.user_id = user_id;
    }

    public int getExercise_count(){
        return exercise_count;
    }

    public void setExercise_count(int exercise_count){
        this.exercise_count = exercise_count;
    }

    public int getStar_count(){
        return star_count;
    }

    public void setStar_count(int star_count){
        this.star_count = star_count;
    }

    public int getDay_count(){
        return day_count;
    }

    public void setDay_count(int day_count){
        this.day_count = day_count;
    }

    public int getCurrent_hurt_count(){
        return current_hurt_count;
    }

    public void setCurrent_hurt_count(int current_hurt_count){
        this.current_hurt_count = current_hurt_count;
    }

    public String getCurrentDate() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        Calendar c = Calendar.getInstance();

        return formater.format(c.getTime());
    }
}
