package com.example.chic_it_app.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chic_it_app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Post> mPosts;
    private PostAdapter postAdapter;
    private SearchView searchView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext() , mPosts);
        recyclerView.setAdapter(postAdapter);
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        readPosts();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPost(newText);
                return true;
            }
        });
        return view;
    }



    private void readPosts() {

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

    private void filterPost(String text) {
        List<Post> filterList = new ArrayList<>();
        for(Post post : mPosts){
            if(post.getDescription().contains(text) || post.getStore().contains(text) ){
                filterList.add(post);
            }
            else if(text.contains("-") && text.indexOf("-")!=text.length()-1){
                if(text.endsWith("$")){
                    String text_new = text.replace("$","");
                    text = text_new;
                }
                if(text.endsWith("₪")){
                    String text_new = text.replace("₪","");
                    text = text_new;
                }
                int x = text.indexOf("-");
                String first = text.substring(0,x);
                String second = text.substring(x+1,text.length());
                String p = post.getPrice();
                int Price = Integer.decode(p);
                if(Integer.valueOf(first) <=Integer.valueOf(second)){
                    if(Price>= Integer.valueOf(first)&&Price<=Integer.valueOf(second)){
                        filterList.add(post);
                    }
                }
                if(Integer.valueOf(first) >=Integer.valueOf(second)){
                    if(Price<=Integer.valueOf(first)&& Price>=Integer.valueOf(second)){
                        filterList.add(post);
                    }
                }
            }
            //if you search price from 0 until your text number
            else if(!text.contains("-") && Character.isDigit(text.charAt(0))){
                //if this it price $ - just posts with $
                if(text.endsWith("$")){
                    String text_new = text.replace("$","");
                    text = text_new;
                    if(post.getPrice().endsWith("$")){
                        post.getPrice().replace("$","");
                        if(Integer.valueOf(post.getPrice())>= 0 && Integer.valueOf(post.getPrice())<=Integer.valueOf(text)){
                            filterList.add(post);
                        }
                    }
                }
                //if this it price ₪- just posts with ₪
                else if(text.endsWith("₪")){
                    String text_new = text.replace("₪","");
                    text = text_new;
                    if(post.getPrice().endsWith("₪")){
                        post.getPrice().replace("₪","");
                        if(Integer.valueOf(post.getPrice())>= 0 && Integer.valueOf(post.getPrice())<=Integer.valueOf(text)){
                            filterList.add(post);
                        }
                    }
                }
                //if you serach price without $ or ₪
                else if(Integer.valueOf(post.getPrice())>= 0 && Integer.valueOf(post.getPrice())<=Integer.valueOf(text)){
                    filterList.add(post);
                }
            }

        }
        if(!filterList.isEmpty()){
            postAdapter.setFilter(filterList);
        }
    }

}