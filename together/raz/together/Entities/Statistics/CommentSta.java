package com.together.raz.together.Entities.Statistics;

/**
 * Created by Raz on 4/20/2017.
 */
public class CommentSta {
    private String titlePost = null;
    private String msg = null;

    public String getTitlePost() {
        return titlePost;
    }

    public void setTitlePost(String titlePost) {
        this.titlePost = titlePost;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CommentSta(String titlePost, String msg) {

        this.titlePost = titlePost;
        this.msg = msg;
    }
}
