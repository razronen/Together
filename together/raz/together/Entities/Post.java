package com.together.raz.together.Entities;

import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raz on 1/28/2017.
 */
public class Post implements Serializable{
    private String title;
    private String message;
    private String publisher;
    private String publisherID;
    private String publisherEntity;
    private String publisherIcon;
    private String date;
    private Boolean commentsCreated = false;
    private Boolean answered = false;
    private Boolean isPublic;
    private int shown_comments = 0;
    private String link = "";
    private String imageX = "";
    private String imageY = "";


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String image = "";

    public Boolean getAnswered() {
        return answered;
    }

    public void setAnswered(Boolean answered) {
        this.answered = answered;
    }

    private String searched = "";
    private Integer color;

    public String getSearched() {
        return searched;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public void setSearched(String searched) {
        this.searched = searched;
    }

    public int getShown_comments() {
        return shown_comments;
    }

    public void setShown_comments(int shown_comments) {
        this.shown_comments = shown_comments;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getPublisherEntity() {
        return publisherEntity;
    }

    public void setPublisherEntity(String publisherEntity) {
        this.publisherEntity = publisherEntity;
    }

    public String getPublisherIcon() {
        return publisherIcon;
    }

    public void setPublisherIcon(String publisherIcon) {
        this.publisherIcon = publisherIcon;
    }

    public Post(Post post) {
        this.title = post.getTitle();
        this.message = post.getMessage();
        this.publisher = post.getPublisher();
        this.publisherID = post.getPublisherID();
        this.date = post.getDate();
        this.id = post.getId();
        this.isPublic = post.getIsPublic();
        this.comments = post.getComments();
        this.shown_comments = post.getShown_comments();
        this.color = post.getColor();
        this.answered = post.getAnswered();
        this.image = post.getImage();
        this.link = post.getLink();
        this.imageX = post.imageX;
        this.imageY = post.imageY;
        this.publisherEntity = post.getPublisherEntity();
        this.publisherIcon = post.getPublisherIcon();


    }

    public Boolean getCommentsCreated() {
        return commentsCreated;
    }

    public void setCommentsCreated(Boolean commentsCreated) {
        this.commentsCreated = commentsCreated;
    }

    private ListView commentsListView = null;

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publisherID='" + publisherID + '\'' +
                ", date='" + date + '\'' +
                ", commentsCreated=" + commentsCreated +
                ", isPublic=" + isPublic +
                ", commentsListView=" + commentsListView +
                ", id='" + id + '\'' +
                ", comments=" + comments +
                '}';
    }

    public ListView getCommentsListView() {
        return commentsListView;
    }

    public void setCommentsListView(ListView commentsListView) {
        this.commentsListView = commentsListView;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    private String id;
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String message, String publisher, String publisherID, String date,
                String id, String isPublic, Integer color,Boolean answered, String image, String link,
                String imageX, String imageY, String publisherEntity, String publisherIcon) {
        this.title = title;
        this.message = message;
        this.publisher = publisher;
        this.publisherID = publisherID;
        this.date = date;
        this.answered = answered;
        this.id = id;
        this.isPublic = (isPublic.equals("true"))?true:false;
        this.color = color;
        this.image = image;
        this.link = link;
        this.imageX = imageX;
        this.imageY = imageY;
        this.publisherEntity = publisherEntity;
        this.publisherIcon = publisherIcon;
    }

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

    public Post(String title, String message, String publisher, String publisherID, String date,
                String isPublic, Integer color,Boolean answered, String image, String link,
                String imageX, String imageY, String publisherEntity, String publisherIcon) {
        this.title = title;
        this.message = message;
        this.publisher = publisher;
        this.publisherID = publisherID;
        this.date = date;
        this.isPublic = (isPublic.equals("true"))?true:false;
        this.color = color;
        this.answered = answered;
        this.image = image;
        this.link = link;
        this.imageX = imageX;
        this.imageY = imageY;
        this.publisherEntity = publisherEntity;
        this.publisherIcon = publisherIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
