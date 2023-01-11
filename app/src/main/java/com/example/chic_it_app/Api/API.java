package com.example.chic_it_app.Api;

import com.example.chic_it_app.Model.Post;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface API
{
    @GET("home_function")
    Call<List<Post>> home_function(

    );
}