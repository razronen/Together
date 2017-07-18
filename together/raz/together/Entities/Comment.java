package com.together.raz.together.Entities;

/**
 * Created by Raz on 2/1/2017.
 */
public class Comment {
    private String postID;
    private String message;
    private String publisher;
    private String publisherID;
    private String publisherEntity;

    public String getPublisherEntity() {
        return publisherEntity;
    }

    public void setPublisherEntity(String publisherEntity) {
        this.publisherEntity = publisherEntity;
    }

    private String date;
    private String id;
    private String image = "";
    private String link = "";
    private String imageX = "";
    private String imageY = "";

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

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public Comment(String message, String publisher, String publisherID, String date, String postID, String image, String link,
                   String imageX, String imageY, String publisherEntity) {
        this.message = message;
        this.publisher = publisher;
        this.publisherID = publisherID;
        this.date = date;
        this.postID = postID;
        this.image = image;
        this.link = link;
        this.imageX = imageX;
        this.imageY = imageY;
        this.publisherEntity = publisherEntity;
    }

    public Comment(String message, String publisher, String publisherID, String date, String postID, String id, String image,
                   String link, String imageX, String imageY, String publisherEntity) {

        this.message = message;
        this.publisher = publisher;
        this.publisherID = publisherID;
        this.date = date;
        this.postID = postID;
        this.id = id;
        this.image = image;
        this.link = link;
        this.imageX = imageX;
        this.imageY = imageY;
        this.publisherEntity = publisherEntity;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(String publisherID) {
        this.publisherID = publisherID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
