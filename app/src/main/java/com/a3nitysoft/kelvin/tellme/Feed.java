package com.a3nitysoft.kelvin.tellme;

import java.util.Date;

public class Feed extends FeedId{
    private String name, email, message, feed_id;
    public Date timestamp;
    public Feed(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Feed(String name, String email, String message, String feed_id){
        this.name = name;
        this.email = email;
        this.message = message;
        this.feed_id = feed_id;
        this.timestamp = timestamp;
    }
}
