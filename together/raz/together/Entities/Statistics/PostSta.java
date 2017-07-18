package com.together.raz.together.Entities.Statistics;

/**
 * Created by Raz on 4/20/2017.
 */
public class PostSta {
    private String title = null;
    private String msg = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PostSta(String title, String msg) {

        this.title = title;
        this.msg = msg;
    }
}
