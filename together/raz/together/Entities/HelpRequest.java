package com.together.raz.together.Entities;

/**
 * Created by Raz on 2/1/2017.
 */
public class HelpRequest {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HelpRequest(String time, String child_id, String message, String child_name) {
        this.time = time;
        this.child_id = child_id;
        this.message = message;
        this.child_name = child_name;
    }

    private String time;
    private String child_id;
    private String message;
    private String child_name;

    public String getChild_name() {
        return child_name;
    }

    public void setChild_name(String child_name) {
        this.child_name = child_name;
    }
}
