package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    private Button login;
    private Button toSignup;
    EditText emailID, password;
    FirebaseAuth mFirebaseAuth;
    private CheckBox rememberMe;
    private ProgressDialog loadingBar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        toSignup = findViewById(R.id.signUp);

        loadingBar = new ProgressDialog(this);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();


            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              userLogin();
            }
        });
        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignup();
            }
        });

    }


    public void openSignup() {
        Intent intent = new Intent(this, Signup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null){
            if (mFirebaseAuth.getCurrentUser().isEmailVerified()){
                openHomeScreen();
            }
        }
    }
    public void openHomeScreen(){
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    public void userLogin(){
        String email = emailID.getText().toString();
        String pwd = password.getText().toString();

        if (email.isEmpty()) {
            emailID.setError("please type a kth email");
            emailID.requestFocus();
        } else if (pwd.isEmpty()) {
            password.setError("Please enter your password");
            password.requestFocus();

        } else if (email.isEmpty() && pwd.isEmpty()) {
            emailID.setError("please type a kth email");
            emailID.requestFocus();
            password.setError("Please enter your password");
            password.requestFocus();
        } else  {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please wait, logging in . . .");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mFirebaseAuth.getCurrentUser().isEmailVerified()){
                            openHomeScreen();
                            Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "Please verify your email" , Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();
                        }


                    } else {
                        String message = task.getException().getMessage();

                        Toast.makeText(MainActivity.this, "Error occurred:" + message, Toast.LENGTH_LONG).show();

                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
}
