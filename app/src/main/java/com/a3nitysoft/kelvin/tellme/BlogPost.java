package com.a3nitysoft.kelvin.tellme;

import java.util.Date;

public class BlogPost extends BlogPostId {

    private String head, tag, image_url, source, video, section, intro, body,user_id;
    public Date timestamp;
    public BlogPost(){

    }

    public BlogPost(String head, String tag, String image_url, String source, String video, String section, String intro, String body, Date timestamp,String user_id) {
        this.head = head;
        this.tag = tag;
        this.source = source;
        this.video = video;
        this.section = section;
        this.intro = intro;
        this.body = body;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.image_url = image_url;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }



}
