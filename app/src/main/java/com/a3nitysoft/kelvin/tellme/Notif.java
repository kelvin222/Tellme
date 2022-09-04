package com.a3nitysoft.kelvin.tellme;

import java.util.Date;

public class Notif extends NotifId {
    private String from, not_id, type, title, message;
    public Date timestamp;
    public Notif(){

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Notif(String from, String not_id, String type, String title, String message, Date timestamp) {
        this.from = from;
        this.not_id = not_id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getNot_id() {
        return not_id;
    }

    public void setNot_id(String not_id) {
        this.not_id = not_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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


}
