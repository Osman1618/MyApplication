package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Signup extends AppCompatActivity {


    private Button register;
    private ImageView back;
    private EditText newemail, newPassword, newPassWordConfirm;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mFirebaseAuth = FirebaseAuth.getInstance();
        newemail = findViewById(R.id.newEmail);
        newPassword = findViewById(R.id.newPassword);
        newPassWordConfirm = findViewById(R.id.newPasswordConfirm);
        register = findViewById(R.id.register);

        back = findViewById(R.id.back_login);
        loadingBar = new ProgressDialog(this);




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });


    }

    private void registerUser() {
        String email = newemail.getText().toString();
        String pwd = newPassword.getText().toString();
        String pwdConfirm = newPassWordConfirm.getText().toString();


        if (email.isEmpty()) {
            newemail.setError("Please type a kth email");
            newemail.requestFocus();


        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newemail.setError("Please enter a valid email");
            newemail.requestFocus();

        } else if (pwd.isEmpty()) {
            newPassword.setError("Please enter your password");
            newPassword.requestFocus();


        }else if ( !(pwd.equals(pwdConfirm))){

            newPassWordConfirm.setText("");
            newPassWordConfirm.setError("passwords must match");
            newPassWordConfirm.requestFocus();
        }
        else {

            loadingBar.setTitle("Sign up");
            loadingBar.setMessage("please wait, setting up your new account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            // video 13 in coding cafe list shows how to do the xPs with HashMap.
                                            updateUserInfo();
                                            Toast.makeText(Signup.this, "Registration Success" , Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                        } else {
                                            Toast.makeText(Signup.this, "Error occurred:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                            loadingBar.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(Signup.this, "SignUp Unsuccessful:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mFirebaseAuth.getCurrentUser() != null) {
            openLogin();

        }
    }

    public void openLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void openHomeScreen() {
        Intent mainIntent = new Intent(Signup.this, HomeScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    public void updateUserInfo(){
        Intent intent = new Intent(Signup.this, updateInfo.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
