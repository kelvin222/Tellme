package com.a3nitysoft.kelvin.tellme;

public class Follow {
    public String name, thumb, about, state, uid;

    public Follow(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }




    public Follow(String name, String thumb, String about, String state, String uid) {
        this.name = name;
        this.thumb = thumb;
        this.about = about;
        this.state = state;
        this.uid = uid;
    }
}
