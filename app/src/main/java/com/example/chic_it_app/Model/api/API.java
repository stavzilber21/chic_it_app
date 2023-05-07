package com.example.chic_it_app.Model.api;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @GET("getUserDetails")
    Call<User> getUserDetails(
            @Query("uid") String uid
    );

    @GET("getPostDetails")
    Call<Post> getPostDetails(
            @Query("pid") String pid
    );

    @FormUrlEncoded
    @POST("savePost")
    Call<ResponseBody> savePost(
            @Field("uid") String uid,
            @Field("pid") String pid
    );

    @GET("countPost")
    Call<Integer> countPost(
            @Query("uid") String uid
    );

    @GET("check")
    Call<ResponseBody> check(
            @Query("uid") String uid,
            @Query("pid") String pid
    );

    @GET("getMyPhoto")
    Call<List<Post>> getMyPhoto(
            @Query("uid") String uid

    );

    @GET("mySavedPosts")
    Call<List<Post>> mySavedPosts(
            @Query("uid") String uid

    );

    @GET("homePosts")
    Call<List<Post>> homePosts(
    );

    @FormUrlEncoded
    @POST("addUser")
    Call<ResponseBody> addUser(
            @Field("username") String username,
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("editProfile")
    Call<ResponseBody> editProfile(
            @Field("uid") String uid,
            @Field("gender") String gender,
            @Field("size") String size,
            @Field("username") String username,
            @Field("fullname") String fullname,
            @Field("imageurl") String imageurl
    );

    @FormUrlEncoded
    @POST("deletePost")
    Call<ResponseBody> deletePost(
            @Field("pid") String pid,
            @Field("publisher") String publisher,
            @Field("uid") String uid
    );

    @FormUrlEncoded
    @POST("makePost")
    Call<ResponseBody> makePost(
//            @Field("userid") String userid,
            @Field("imageurl") String imageurl,
            @Field("description") String description,
            @Field("store") String store,
            @Field("price") String price,
            @Field("type") String type,
            @Field("uid") String uid
    );

}
