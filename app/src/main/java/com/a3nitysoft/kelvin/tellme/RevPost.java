package com.a3nitysoft.kelvin.tellme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RevPost extends RevPostId {

    private String head, tag, image_url, source, section, username, body, rev_id, user_id;
    private List<String> audience = new ArrayList<>();

    public Date timestamp;
    public RevPost() {

    }

    public RevPost(String head, String tag, String image_url, String source, String section, String username, String body, Date timestamp, String rev_id, String user_id, List<String> audience) {
        this.head = head;
        this.tag = tag;
        this.source = source;
        this.section = section;
        this.username = username;
        this.body = body;
        this.audience = audience;
        this.timestamp = timestamp;
        this.rev_id = rev_id;
        this.user_id = user_id;
        this.image_url = image_url;
    }
    public String getRev_id() {
        return rev_id;
    }

    public void setRev_id(String rev_id) {
        this.rev_id = rev_id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<String> getAudience() {
        return audience;
    }

    public void setAudience(List<String> audience) {
        this.audience = audience;
    }
}
