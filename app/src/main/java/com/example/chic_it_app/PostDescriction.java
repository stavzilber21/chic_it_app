package com.example.chic_it_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chic_it_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostDescriction extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner item;
    Spinner size;
    Spinner length;
    String [] items = {"skirt","dress","shirt","pants"};
    String [] sizes = {"s", "m" ,"l"};
    String [] lengths = {"short","midi","long"};
    String choose_item;
    String choose_size;
    String choose_length;
    Button finish;
//    PostActivity p =new PostActivity();



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_descriction);

        item = (Spinner) findViewById(R.id.item);
        size = (Spinner) findViewById(R.id.size);
        length = (Spinner) findViewById(R.id.length);

        finish = findViewById(R.id.finish);

        item.setOnItemSelectedListener(this);
        size.setOnItemSelectedListener(this);
        length.setOnItemSelectedListener(this);


        ArrayAdapter bb = new ArrayAdapter(this,android.R.layout.simple_spinner_item, items);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        item.setAdapter(bb);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,sizes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        size.setAdapter(aa);

        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter cc = new ArrayAdapter(this,android.R.layout.simple_spinner_item,lengths);
        cc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        length.setAdapter(cc);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_item = item.getSelectedItem().toString();
                choose_size = size.getSelectedItem().toString();
                choose_length = length.getSelectedItem().toString();
                PostActivity.add_Item(choose_item,choose_size,choose_length);

//                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
//                String postId = ref.push().getKey();
//
//                HashMap<String , Object> map = new HashMap<>();
//                map.put("item" , choose_item);
//                map.put("size" , choose_size);
//                map.put("length" , choose_length);
//                ref.child(postId).updateChildren(map);

                startActivity(new Intent(PostDescriction.this , PostActivity.class));
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}