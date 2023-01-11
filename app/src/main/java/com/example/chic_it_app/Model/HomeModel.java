package com.example.chic_it_app.Model;

import android.app.Activity;

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

public class HomeModel {
    private Fragment fragment;

    public HomeModel(Fragment fragment) {
        this.fragment = fragment;

    }

    public void readPosts(List<Post> mPosts, PostAdapter postAdapter) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (TextUtils.isEmpty(search_bar.getText().toString())){
                mPosts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    mPosts.add(post);
                    Collections.reverse(mPosts);
                }

                postAdapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}