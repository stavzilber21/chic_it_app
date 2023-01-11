package com.example.chic_it_app.Model;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chic_it_app.Adapter.PostAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostDetailModel {
    private Fragment fragment;


    public PostDetailModel(Fragment fragment) {
        this.fragment = fragment;

    }
    public void user_id(String postId, List<Post> postList, PostAdapter postAdapter){
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                postList.add(dataSnapshot.getValue(Post.class));

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
