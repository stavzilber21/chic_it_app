package com.example.chic_it_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.ETC1Util;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chic_it_app.Model.RegisterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    //This class is for a new user of the application logging in for the first time.
    RegisterModel model = new RegisterModel(this);
    EditText username, fullname, email, password, phone;
    Button register;
    TextView txt_login;
    FirebaseAuth auth;

    ProgressDialog pd;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        //if I already register to application
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        //after you fill the details
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setCancelable(true);
                builder.setTitle("You have been contacted via your phone");
                builder.setMessage("");
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    pd = new ProgressDialog(RegisterActivity.this);
                    //to display the progress of an action that is loading.
                    pd.setMessage("Please wait...");
                    pd.show();

                    String str_username = username.getText().toString();
                    String str_fullname = fullname.getText().toString();
                    String str_email = email.getText().toString();
                    String str_password = password.getText().toString();
                    String str_phone = phone.getText().toString();

                    if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                            || TextUtils.isEmpty(str_email) ||TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_phone)) {
                        Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    } else if (str_password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                    }
                    else if (str_phone.length() < 12) {
                        Toast.makeText(RegisterActivity.this, "Phone must have 12 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        model.register(str_username, str_fullname, str_email, str_password,str_phone,auth,pd);
                    }
                });

                builder.setNegativeButton("Deny", (dialog, which) -> {
                    Toast.makeText(RegisterActivity.this, "You will not be able to enter the application!", Toast.LENGTH_SHORT).show();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }


}