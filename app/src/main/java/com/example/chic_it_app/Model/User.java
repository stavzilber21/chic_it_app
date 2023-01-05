package com.example.chic_it_app.Model;



public class User {

    private String fullname;
    private String email;
    private String username;
    //    private String bio;
    private String imageurl;
    private String id;
    private String gender;
    private String size;
    private String phone;

    public User() {
    }

    public User(String name, String email, String username, String bio, String imageurl, String id,String gender,String size) {
        this.fullname = name;
        this.email = email;
        this.username = username;
//        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
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