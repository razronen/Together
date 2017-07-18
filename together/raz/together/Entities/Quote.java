package com.together.raz.together.Entities;

/**
 * Created by Raz on 2/1/2017.
 */
public class Quote {
    private String num = "-1";
    private String content = null;
    private String creator = null;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Quote(String num, String content, String creator) {

        this.num = num;
        this.content = content;
        this.creator = creator;
    }

    public Quote(String content, String creator) {

        this.content = content;
        this.creator = creator;
    }
}
