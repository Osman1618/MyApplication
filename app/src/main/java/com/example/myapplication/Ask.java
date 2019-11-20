package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Ask extends AppCompatActivity {

    private ImageButton backHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        ImageButton backHome = (ImageButton) findViewById(R.id.back_home);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backhome();
            }
        });
    }
    public void backhome(){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
