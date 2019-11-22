package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Signup extends AppCompatActivity {

    private Button tologin;
    private Button register;
    private EditText newemail, newPassword;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tologin = (Button) findViewById(R.id.toLogIn);
        mFirebaseAuth = FirebaseAuth.getInstance();
        newemail = findViewById(R.id.newEmail);
        newPassword = findViewById(R.id.newPassword);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = newemail.getText().toString();
                String pwd = newPassword.getText().toString();

                if (email.isEmpty()) {
                    newemail.setError("please type a kth email");
                    newemail.requestFocus();
                } else if (pwd.isEmpty()) {
                    newPassword.setError("Please enter your password");
                    newPassword.requestFocus();

                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(Signup.this, "Email and password are empty", Toast.LENGTH_SHORT).show();

                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Signup.this, "SignUp Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(Signup.this, "SignUp successful, please verify your Email", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(Signup.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }

                        }
                    });
                } else {
                    Toast.makeText(Signup.this, "Error Occurred!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });

    }

    public void openLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
