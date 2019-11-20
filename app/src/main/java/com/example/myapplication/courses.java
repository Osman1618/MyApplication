package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class courses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
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
                    openHome();
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
    public void openHome(){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }
}
