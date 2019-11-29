package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeScreen extends AppCompatActivity {

    private Button courses;
    private Button ask;
    private Button answer;
    private Button logOut;

    FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        logOut = findViewById(R.id.logOut);
        courses = findViewById(R.id.courses);
        ask = findViewById(R.id.ask);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        answer = findViewById(R.id.answer);
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
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent (HomeScreen.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish();
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuestionFeed();
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
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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
    public void openQuestionFeed(){
        Intent intent = new Intent(this, question_feed.class);
        startActivity(intent);

    }

}
