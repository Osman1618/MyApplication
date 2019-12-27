package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeScreen extends AppCompatActivity {

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;

    private Button ask, MyQuestions;

    private TextView userNameDrawer, userEmailDrawer ;

    private String currentUserId, currentUserEmail;
    private Button answer;

    private DatabaseReference mUserRef;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        drawerLayout = findViewById(R.id.drawer_in_home_screen);
        navigationView = findViewById(R.id.navigation_view_drawer);



        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        userEmailDrawer = (TextView) navView.findViewById(R.id.user_email_drawer);
        userNameDrawer = (TextView) navView.findViewById(R.id.user_name_drawer);
        mfirebaseAuth = FirebaseAuth.getInstance();
        currentUserId = mfirebaseAuth.getCurrentUser().getUid();

        currentUserEmail = mfirebaseAuth.getCurrentUser().getEmail();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        ask = findViewById(R.id.ask);
        MyQuestions = findViewById(R.id.myQuestions);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavigationView mainNav = findViewById(R.id.main_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        mainNav.setOnNavigationItemSelectedListener(mainNavListener);

        answer = findViewById(R.id.answer);
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAskScreen();
            }
        });
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuestionFeed();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelecter(menuItem);
                return false;
            }
        });


        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userNameDrawer.setText(dataSnapshot.child("fullname").getValue().toString());
                    userEmailDrawer.setText(currentUserEmail);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        MyQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyQuestionsFeed();
            }
        });

    }

    private void UserMenuSelecter(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.update_information:

                openUpdateScreen();
                break;
            case R.id.settings_nav_drawer:

                opensettingsScreen();
                break;
            case R.id.feedBack:

                openFeedBack();
                break;
            case R.id.logout_drawer:

                signOutAccount();
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_leaderBoard:
                    openLeaderBoardScreen();
                    break;

                case R.id.nav_mail:
                    openMailScreen();

                    break;
                case R.id.nav_home:
                    return true;

            }
            return true;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mainNavListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_menue:
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
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

    public void openMyQuestionsFeed() {
        Intent intent = new Intent(this, MyQuestions.class);
        startActivity(intent);

    }

    public void openUpdateScreen() {
        Intent intent = new Intent(HomeScreen.this, updateInfo.class);
        startActivity(intent);

    }

    public void opensettingsScreen() {

    }

    public void openFeedBack() {


    }

    private void signOutAccount() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeScreen.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void openAskScreen() {
        Intent intent = new Intent(this, Ask.class);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

    public void openQuestionFeed() {
        Intent intent = new Intent(this, question_feed.class);
        startActivity(intent);

    }

}
