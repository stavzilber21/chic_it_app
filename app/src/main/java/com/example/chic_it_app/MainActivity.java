package com.example.chic_it_app;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener  {
    Button user, creating_content;
    ImageView more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = (Button) findViewById(R.id.user);
        creating_content = (Button) findViewById(R.id.creating_content);
        more = (ImageView) findViewById(R.id.more);
        //to connect the buttoms to xml file
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserActivity.class));
            }
        });

        creating_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreatingcontentActivity.class));
            }
        });

        more.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                showPopup(v);

            }
        });

    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(MainActivity.this);
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }


    public void dialog_exit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("you sure that you want to exit?");
        builder.setMessage("");
        builder.setPositiveButton("yes please", (dialog, which) ->
        {
            Toast.makeText(MainActivity.this, "bye-bye!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this , LoginActivity.class));
        });

        builder.setNegativeButton("no", (dialog, which) ->
        {
            Toast.makeText(MainActivity.this, "good to have you back!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(MainActivity.this , MainActivity.class));
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                startActivity(new Intent(MainActivity.this , EditProfileActivity.class));
                break;
            case R.id.logOut:
                dialog_exit();
                break;
            default:
                return false;
        }
        return  true;
    }






}