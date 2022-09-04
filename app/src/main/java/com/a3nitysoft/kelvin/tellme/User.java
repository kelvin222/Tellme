package com.a3nitysoft.kelvin.tellme;



public class User {

    public String name, image, about, state;

    public User(){

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

    public User(String name, String image, String about, String state) {
        this.name = name;
        this.image = image;
        this.about = about;
        this.state = state;
    }
}
