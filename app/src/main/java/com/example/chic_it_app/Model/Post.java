package com.example.chic_it_app.Model;

public class Post {

    private String description;
    private String store;
    private String price;
    private String imageurl;
    private String postid;
    private String publisher;

    public Post() {
    }

    public Post(String description, String imageurl, String postid, String publisher,String store, String price) {
        this.description = description;
        this.imageurl = imageurl;
        this.postid = postid;
        this.publisher = publisher;
        this.store = store;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}