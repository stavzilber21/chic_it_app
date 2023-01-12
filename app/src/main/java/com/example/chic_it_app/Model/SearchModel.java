package com.example.chic_it_app.Model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chic_it_app.Adapter.PostAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class SearchModel {
    /*This class is the model of the "search page" all the connection to Firebase is done through this class.*/

    private Fragment fragment;

    public SearchModel(Fragment fragment) {
        this.fragment = fragment;

    }
    //View all posts
    public void readPosts(List<Post> mPosts, PostAdapter postAdapter) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    mPosts.add(post);
                    Collections.reverse(mPosts);
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
