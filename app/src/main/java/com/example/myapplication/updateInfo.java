package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class updateInfo extends AppCompatActivity {


    private EditText userName, fullName, programName, programCode;
    private Button done;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private int xps;

    private ProgressDialog loadingBar;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userName = findViewById(R.id.user_name);
        fullName = findViewById(R.id.full_name);
        programName = findViewById(R.id.programName);
        programCode = findViewById(R.id.programCode);
        done = findViewById(R.id.done);

        xps = 0;


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveUserInformation();
            }
        });

    }

    private void saveUserInformation() {
        String username = userName.getText().toString();
        String fullname = fullName.getText().toString();
        String programcode = programCode.getText().toString();
        String programname = programName.getText().toString();
        String email = mAuth.getCurrentUser().getEmail().toString();

        loadingBar = new ProgressDialog(this);


        if (TextUtils.isEmpty(username)) {
            userName.setError("Please write your username");
            userName.requestFocus();

        }
        if (TextUtils.isEmpty(fullname)) {
            fullName.setError("Please write your full name");
            fullName.requestFocus();

        }
        if (TextUtils.isEmpty(programcode)) {
            programCode.setError("Please write your program code");
            programCode.requestFocus();

        }
        if (TextUtils.isEmpty(programname)) {
            programName.setError("Please write your program name");
            programName.requestFocus();
        } else {

            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait while we save your information");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("fullname", fullname);
            userMap.put("programcode", programcode);
            userMap.put("programname", programname);
            userMap.put("points", xps);


            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        toLoginActivity();
                        Toast.makeText(updateInfo.this, "Information updated", Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();
                    } else {
                        Toast.makeText(updateInfo.this, "Error Occurred" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();
                    }
                }
            });


        }

    }

    private void toLoginActivity() {
        Intent intent = new Intent(updateInfo.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
