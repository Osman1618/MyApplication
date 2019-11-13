package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button login;
    EditText email, password;
    CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        remember = findViewById(R.id.rememberMe);
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if(checkbox.equals("true")){

            Intent intent = new Intent (MainActivity.this, HomeScreen.class);
            startActivity(intent);
        } else if (checkbox.equals("false")){
            Toast.makeText(this, "Please sign in.", Toast.LENGTH_SHORT).show();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeScreen();
            }
        });
       remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (buttonView.isChecked()){
                   SharedPreferences preferences = getSharedPreferences("chackbox", MODE_PRIVATE);
                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putString( "remember", "true");
                   editor.apply();
                   Toast.makeText(MainActivity.this, "checked", Toast.LENGTH_SHORT).show();

               }else if (!buttonView.isChecked()){
                   SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                   SharedPreferences.Editor editor = preferences.edit();
                   editor.putString("remember", "false");
                   editor.apply();
               }
           }
       });


    }
    public void openHomeScreen (){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }
}
