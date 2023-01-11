package com.example.chic_it_app.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chic_it_app.Model.HomeModel;
import com.example.chic_it_app.Model.PostDetailModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.R;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {
    PostDetailModel model = new PostDetailModel(this);
    private String postId;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("postid", "none");
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        model.user_id(postId,postList,postAdapter);



        return view;
    }
}