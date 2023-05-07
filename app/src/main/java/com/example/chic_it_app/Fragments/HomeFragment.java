package com.example.chic_it_app.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.example.chic_it_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Post> mPosts;
    private PostAdapter postAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , mPosts);
        recyclerView.setAdapter(postAdapter);

        readPosts();

        return view;
    }

    private void readPosts() {
        Call<List<Post>> call = RetrofitClient.getInstance().getAPI().homePosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                mPosts.clear();
                List<Post> postsList = response.body();
                if (postsList != null) {
                    mPosts.addAll(postsList);
                    Collections.reverse(mPosts);
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

            }
        });
    }
}