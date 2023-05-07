package com.example.chic_it_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.chic_it_app.Model.api.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    Call<ResponseBody> call = RetrofitClient.getInstance().getAPI().addUser(str_username, str_fullname, str_email, str_phone, str_password);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("add user", "success");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("add user", t.getMessage());
                        }
                    });
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}