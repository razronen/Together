package com.together.raz.together.Entities;

/**
 * Created by Raz on 3/16/2017.
 */
public class Message {
    private String childID;
    private Integer num;
    private String time;
    private String message;
    private String image = "";
    private String link = "";
    private String authorID;
    private String authorName;
    private String authorEntity;

    public String getImageX() {
        return imageX;
    }

    public void setImageX(String imageX) {
        this.imageX = imageX;
    }

    public String getImageY() {
        return imageY;
    }

    public void setImageY(String imageY) {
        this.imageY = imageY;
    }

    private String imageX = "";
    private String imageY = "";

    @Override
    public String toString() {
        return "Message{" +
                "childID='" + childID + '\'' +
                ", num=" + num +
                ", time='" + time + '\'' +
                ", message='" + message + '\'' +
                ", image='" + image + '\'' +
                ", link='" + link + '\'' +
                ", authorID='" + authorID + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorEntity='" + authorEntity + '\'' +
                '}';
    }

    public Message(String childID, Integer num, String time, String message, String image,
                   String link, String authorID, String authorName, String authorEntity, String imageX,
                   String imageY) {
        this.childID = childID;
        this.num = num;
        this.time = time;
        this.message = message;
        this.image = image;
        this.link = link;
        this.authorID = authorID;
        this.authorName = authorName;
        this.authorEntity = authorEntity;
        this.imageX = imageX;
        this.imageY = imageY;
    }

    public String getChildID() {

        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEntity() {
        return authorEntity;
    }

    public void setAuthorEntity(String authorEntity) {
        this.authorEntity = authorEntity;
    }

}
