package com.example.chic_it_app.Model;



public class User {
    //This class holds the user object and all its fields.

    private String fullname;
    private String email;
    private String username;
    private String imageurl;
    private String uid;
    private String gender;
    private String size;
    private String phone;

    public User() {
    }

    public User(String name, String email, String username, String imageurl, String uid,String gender,String size) {
        this.fullname = name;
        this.email = email;
        this.username = username;
        this.imageurl = imageurl;
        this.uid = uid;
        this.gender = gender;
        this.size = size;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}