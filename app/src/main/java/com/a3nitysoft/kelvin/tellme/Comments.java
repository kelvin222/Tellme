package com.a3nitysoft.kelvin.tellme;

import java.util.Date;

import android.support.v4.widget.SwipeRefreshLayout;
public class Comments extends CommentsId {


    private String message, user_id, comm_id, review_id, name, image, mtime, mdate;
    private long timestamp;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Comments(){

    }

    public Comments(String message, String name, String image, String user_id, String mtime,String mdate, String comm_id, String review_id, long timestamp) {
        this.message = message;
        this.name = name;
        this.image = image;
        this.user_id = user_id;
        this.mtime = mtime;
        this.mdate = mdate;
        this.comm_id = comm_id;
        this.review_id = review_id;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getComm_id() {
        return comm_id;
    }

    public void setComm_id(String comm_id) {
        this.comm_id = comm_id;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
