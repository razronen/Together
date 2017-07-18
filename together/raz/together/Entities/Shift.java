package com.together.raz.together.Entities;

/**
 * Created by Raz on 4/10/2017.
 */
public class Shift {
    private String id;
    private String start;
    private String end;
    private String psycho_id;
    private String psycho_deviceId;
    private String psycho_name;
    private String psycho_mail;

    @Override
    public String toString() {
        return "Shift{" +
                "id='" + id + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", psycho_id='" + psycho_id + '\'' +
                ", psycho_deviceId='" + psycho_deviceId + '\'' +
                ", psycho_name='" + psycho_name + '\'' +
                ", psycho_mail='" + psycho_mail + '\'' +
                '}';
    }

    public Shift(String id, String start, String end, String psycho_id,String psycho_deviceId,
                 String psycho_name, String psycho_mail) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.psycho_id = psycho_id;
        this.psycho_deviceId = psycho_deviceId;
        this.psycho_name = psycho_name;
        this.psycho_mail = psycho_mail;
    }

    public Shift(String start, String end, String psycho_id,String psycho_deviceId,
                 String psycho_name, String psycho_mail) {
        this.start = start;
        this.end = end;
        this.psycho_id = psycho_id;
        this.psycho_deviceId = psycho_deviceId;
        this.psycho_name = psycho_name;
        this.psycho_mail = psycho_mail;
    }

    public String getPsycho_deviceId() {
        return psycho_deviceId;
    }

    public void setPsycho_deviceId(String psycho_deviceId) {
        this.psycho_deviceId = psycho_deviceId;
    }

    public String getId() {

        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPsycho_id() {
        return psycho_id;
    }

    public void setPsycho_id(String psycho_id) {
        this.psycho_id = psycho_id;
    }

    public String getPsycho_name() {
        return psycho_name;
    }

    public void setPsycho_name(String psycho_name) {
        this.psycho_name = psycho_name;
    }

    public String getPsycho_mail() {
        return psycho_mail;
    }

    public void setPsycho_mail(String psycho_mail) {
        this.psycho_mail = psycho_mail;
    }
}
