package com.example.chic_it_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chic_it_app.Adapter.PostAdapter;
import com.example.chic_it_app.Adapter.UserAdapter;
import com.example.chic_it_app.Model.Post;
import com.example.chic_it_app.Model.User;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SearchActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private List<Post> mPosts;
    private PostAdapter postAdapter;
    private SearchView searchView;
    private List<String> idList;
    private FirebaseUser fUser;
    private List<String> sizeGender;


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(this , mPosts);
        recyclerView.setAdapter(postAdapter);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        idList = new ArrayList<>();
        sizeGender = new ArrayList<>();
        getFollowings();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    // if you what to upload a new post
                    case R.id.nav_add :
                        startActivity(new Intent(SearchActivity.this , PostActivity.class));
                        break;
                    case R.id.nav_search:
                        startActivity(new Intent(SearchActivity.this , UserSearch.class));
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(SearchActivity.this , LoginActivity.class));
//                        dialog_exit();
                        //if you want to search posts by description
                    case R.id.nav_home :
                        startActivity(new Intent(SearchActivity.this , SearchActivity.class));
                        break;
                    //to see your profile
                    case R.id.nav_profile :
                        startActivity(new Intent(SearchActivity.this , ProfileActivity.class));
                        break;
                }

                return  true;
            }});
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter_post(newText);
                return true;
            }
        });


    }

    private void readPosts() {
        Call<List<Post>> call = RetrofitClient.getInstance().getAPI().homePosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                mPosts.clear();
                List<Post> post_unfollow = new ArrayList<>();
                List<Post> postsList = response.body();
                if (postsList != null) {
                    for(Post post : postsList){
                        if (idList != null && idList.contains(post.getPublisher())) {
                            mPosts.add(post);
                        }
                        else{
                            post_unfollow.add(post);
                        }
                    }
                    mPosts.addAll(post_unfollow);

                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                // Handle failure
            }
        });

    }

    private void getFollowings() {
        Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getFollowings(fUser.getUid());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                idList.clear();
                if (response.isSuccessful()) {
                    String ans = response.body().byteStream().toString();
                    int start = ans.indexOf("=");
                    int end = ans.indexOf("]");
                    String[] followingList = ans.substring(start+1,end).split(",");
                    if (followingList != null) {
                        for(String str: followingList){
                            if(!str.equals(fUser.getUid()))
                                idList.add(str);
                        }
                    }
                    readPosts();
                } else {
                    Log.d("Fail", "Request failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });
    }


    private void filter_post(String text){
        List<Post> filterList = new ArrayList<>();
        for(Post post : mPosts){
            sizeAndGender(post.getPublisher());
            if(text=="M"||text=="S"||text=="L"||text=="female"||text=="male"){

                if(sizeGender.get(0)==text||sizeGender.get(1)==text){
                    filterList.add(post);
                }
                postAdapter.setFilter(filterList);
            }
            else{
            Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().getPostItems(post.getPostid());
            call.enqueue(new Callback<ResponseBody>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ResponseBody itemsList = response.body();
                    String it = null;
                    JSONObject itemObject = new JSONObject();
                    try {
                        it = itemsList.string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (it != null) {
                        try {
                            itemObject = new JSONObject(it);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (Iterator<String> iter = itemObject.keys(); iter.hasNext(); ) {
                            int flag=0;
                            String key = iter.next();
                            JSONObject innerObject = null;
                            try {
                                innerObject = (JSONObject) itemObject.get(key);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (Iterator<String> iterator = innerObject.keys(); iterator.hasNext(); ) {
                                String innerKey = iterator.next();
                                String value = null;
                                try {
                                    value = (String) innerObject.get(innerKey);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (value.contains(text) && !filterList.contains(post)) {
                                    filterList.add(post);
                                    flag=1;
                                    break;
                                }
                            }
                            if (flag==1){
                                break;
                            }
                        }
                        postAdapter.setFilter(filterList);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle the failure case
                    // ...
                }
            });
            }
        }
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
        //if(!filterList.isEmpty()){
        postAdapter.setFilter(filterList);
        //}
    }

    //dialog that ask the user before you click.
    public void dialog_exit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("you sure that you want to exit?");
        builder.setMessage("");
        builder.setPositiveButton("yes please", (dialog, which) ->
        {
            Toast.makeText(SearchActivity.this, "bye-bye!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SearchActivity.this , LoginActivity.class));
        });

        builder.setNegativeButton("no", (dialog, which) -> {
            Toast.makeText(SearchActivity.this, "good to have you back!", Toast.LENGTH_SHORT).show();
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void sizeAndGender(String id){
        Call<User> call = RetrofitClient.getInstance().getAPI().getUserDetails(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                sizeGender.add(user.getSize());
                sizeGender.add(user.getGender());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Fail", t.getMessage());
            }
        });

    }


}