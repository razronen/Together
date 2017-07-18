package com.together.raz.together.Entities.Updates;

import org.json.JSONObject;

import java.util.Comparator;

/**
 * Created by Raz on 4/22/2017.
 */
public class Update implements Comparator<Update>{
    public static final String NEW_SHIFT = "SHIFT_UPDATE";
    public static final String NEW_USER = "USER_UPDATE";

    private String update = null;

    private String time = null;

    private Integer num = null;

    public Update(String update, String time, Integer num, JSONObject json) {
        this.update = update;
        this.time = time;
        this.num = num;
        this.json = json;
    }

    public Update() {

    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    private JSONObject json = null;

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }


    @Override
    public int compare(Update lhs, Update rhs) {
        return lhs.getNum() - rhs.getNum();
    }
}
