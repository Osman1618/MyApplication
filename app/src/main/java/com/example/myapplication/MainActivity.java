package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import android.widget.EditText;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private Button signup;




    EditText email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button) findViewById(R.id.login);

        signup = (Button) findViewById(R.id.signUp);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignup();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeScreen();
            }
        });

    }


    public void openHomeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }
    public void openSignup(){
        Intent intent = new Intent (this, Signup.class);
        startActivity(intent);
    }

}
