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

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPosts = new ArrayList<>();
        postAdapter = new PostAdapter(this , mPosts);
        recyclerView.setAdapter(postAdapter);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        readPosts();
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
    private void filter_post(String text){
        List<Post> filterList = new ArrayList<>();
        for(Post post : mPosts){
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
//                        System.out.println(text);
//                        System.out.println("stav:    "+ it);
//                        if(it.contains(text)){
//                            filterList.add(post);
//                        }
                        for (Iterator<String> iter = itemObject.keys(); iter.hasNext(); ) {
                            String key = iter.next();
                            JSONObject innerObject = null;
                            try {
                                innerObject = (JSONObject) itemObject.get(key);
                                System.out.println("stav:    "+ innerObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (Iterator<String> iterator = innerObject.keys(); iterator.hasNext(); ) {
                                String innerKey = iterator.next();
                                String value = null;
                                try {
                                    value = (String) innerObject.get(innerKey);
                                    System.out.println("zilber:    "+ value);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (value.contains(text) && !filterList.contains(post)) {
                                    filterList.add(post);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Handle the failure case
                    // ...
                }
            });
        }
        postAdapter.setFilter(filterList);
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


}