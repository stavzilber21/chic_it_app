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

    EditText username, fullname, email, password, phone;
    Button register;
    TextView txt_login;
    FirebaseAuth auth;
    DatabaseReference reference;
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
                        register(str_username, str_fullname, str_email, str_password,str_phone);
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

    //connect to details of user to firebase
    private void register(final String username, final String fullname, String email, String password, String phone ){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            // to create sub tree in firebase ti Users
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("phone", phone);
                            hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/chicit-a5e00.appspot.com/o/placeholder.png?alt=media&token=e355a742-f8f6-4ca6-b4d4-734dfb6091a3");
                            //to enter the fields of User to tree in the firebase
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }

                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}