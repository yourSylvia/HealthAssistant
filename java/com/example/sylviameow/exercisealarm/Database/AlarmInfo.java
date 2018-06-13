package com.example.sylviameow.exercisealarm.Database;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class AlarmInfo extends RealmObject {
    @PrimaryKey
    private int alarm_id;

    private long time_stamp;

    @Required
    private String time;
    private int alarm_type;


    public int getAlarm_id(){
        return alarm_id;
    }

    public void setAlarm_id(int alarm_id) {
        this.alarm_id = alarm_id;
    }

    public long getTime_stamp(){
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp){
        this.time_stamp = time_stamp;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    // 1: repeat 2: once
    public int getAlarm_type(){
        return alarm_type;
    }

    public void setAlarm_type(int alarm_type){
        this.alarm_type = alarm_type;
    }

    // 1: enable 2: disable
//    public int getAlarm_state(){
//        return alarm_state;
//    }
//
//    public void setAlarm_state(int alarm_state){
//        this.alarm_state = alarm_state;
//    }

}
