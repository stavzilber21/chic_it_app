package com.example.chic_it_app.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.chic_it_app.Model.HomeModel;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*This department is the "home page" of the application where you can view all existing posts.*/
public class HomeFragment extends Fragment {
    HomeModel model = new HomeModel(this);
    private RecyclerView recyclerView;
    private List<Post> mPosts;
    private PostAdapter postAdapter;
    private TextView count_post;

    //called to inflate the layout of the fragment
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        count_post = view.findViewById(R.id.count_post);
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , mPosts);
        recyclerView.setAdapter(postAdapter);

        count_post();
        //to call function from the model.
        model.readPosts(mPosts,postAdapter);

        return view;
    }
/*
A function where we worked with the server. That is, the lock is on the server and the rest of the code is here.
The function actually holds a variable that stores the number of posts.
And when we click on the home screen, we can see the amount.
 */
    public void count_post()
    {
        RequestQueue volleyQueue = Volley.newRequestQueue(getContext());
        String url = "http://192.168.144.68:3000";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    count_post.setText(String.valueOf(response));
                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                }, error -> {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        volleyQueue.add(stringRequest);


    }




}