package com.example.sylviameow.exercisealarm.Database;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserServerInfo extends RealmObject{

    @PrimaryKey
    private String user_id;

    @Required
    private String user_number;
    private String user_username;
    private String user_nickname;
    private String user_platform;

    private int id;

    // The annotated field must be declared final
    @LinkingObjects("serverInfos")
    private final RealmResults<UserState> localStates = getRealm().where(UserState.class)
            .equalTo("user_id", user_id)
            .findAll();


    public String getUser_id(){
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUser_username(){
        return user_username;
    };

    public void setUser_username(String user_username){
        this.user_username = user_username;
    }

    public String getUser_number(){
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getUser_nickname(){
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_platform(){
        return user_platform;
    }

    public void setUser_platform(String user_platform) {
        this.user_platform = user_platform;
    }

}
