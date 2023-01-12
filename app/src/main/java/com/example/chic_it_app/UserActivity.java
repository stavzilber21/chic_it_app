package com.example.chic_it_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.chic_it_app.Fragments.HomeFragment;
import com.example.chic_it_app.Fragments.ProfileFragment;
import com.example.chic_it_app.Fragments.SearchFragment;


public class UserActivity extends AppCompatActivity {
    /*This class represents the permission of a "user" when he open home page, search posts
  and view his profile page.*/

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    //if you to see all the posts in app
                    case R.id.nav_home :
                        selectorFragment = new HomeFragment();
                        break;
                    //if you want to search posts by description
                    case R.id.nav_search :
                        selectorFragment = new SearchFragment();
                        break;
                    //to see your profile
                    case R.id.nav_profile :
                        selectorFragment = new ProfileFragment();
                        break;
                }

                if (selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , selectorFragment).commit();
                }

                return  true;

            }
        });

    }
}