package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreen extends AppCompatActivity {

    private Button courses;
    private Button ask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);

        courses = findViewById(R.id.courses);
        ask = findViewById(R.id.ask);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCourses();
            }
        });
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAskScreen();
            }
        });
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.nav_leaderBoard:
                    openLeaderBoardScreen();
                    break;

                case R.id.nav_mail:
                    openMailScreen();
                    break;
                case R.id.nav_home:
                    break;
            }
            return true;
        }
    };
    public void openLeaderBoardScreen() {
        Intent intent = new Intent(this, LeaderBoard.class);
        startActivity(intent);
    }

    public void openMailScreen() {
        Intent intent = new Intent(this, MailScreen.class);
        startActivity(intent);
    }
    public void openCourses(){
        Intent intent = new Intent(this, courses.class);
        startActivity(intent);
    }
    public void openAskScreen(){
        Intent intent = new Intent(this, Ask.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}
