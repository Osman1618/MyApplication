package com.example.myapplication;
/*
Course Code: for tags.
XPs : updates.

 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Ask extends AppCompatActivity {

    private ImageButton backHome;
    private Button upload, submit;
    private EditText questionBody, questionTitle;
    private Switch anonymous;
    private Boolean picUpload = false;

    private final static int Gallery_pick = 1;

    private static final String TAG = "Ask";
    private ProgressDialog loadingBar;
    private StorageReference questionImagesRef;
    private String body, title;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference QuestionRef;

    private Uri ImageUri;
    private Integer points;
    private ImageView mQuestionImage;
    private String userXps;

    private String saveCurrenDate, saveCurrentTime, questionRandomName, downloadUrl;
    String currentUserId;
    String questionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        questionImagesRef = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");

        loadingBar = new ProgressDialog(this);
        backHome = (ImageButton) findViewById(R.id.back_home);
        upload = findViewById(R.id.Upload_Picture);
        submit = findViewById(R.id.submit);
        questionBody = findViewById(R.id.question);
        questionTitle = findViewById(R.id.question_title);
        anonymous = findViewById(R.id.anonymous);

        mQuestionImage = findViewById(R.id.imageView);
        questionId = questionTitle.getText().toString();


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }


        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateQuestionInfo();

            }
        });

    }

    public void ValidateQuestionInfo() {


        body = questionBody.getText().toString();
        title = questionTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            questionTitle.setError("Required");
            questionTitle.requestFocus();
            Toast.makeText(Ask.this, "Please assign a title to your question", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(body)) {
            questionBody.setError("Required");
            questionBody.requestFocus();
            Toast.makeText(Ask.this, "Please write a description to your question", Toast.LENGTH_SHORT).show();
        } else if (picUpload) {
            loadingBar.setTitle("Question submit");
            loadingBar.setMessage("Your question is being posted");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImagetoFireBaseStorage();


        } else {
            loadingBar.setTitle("Question submit");
            loadingBar.setMessage("Your question is being posted");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            savingQuestionInformation();

        }


    }
    public void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();

            mQuestionImage.setImageURI(ImageUri);
            Toast.makeText(Ask.this, "Image Uploaded", Toast.LENGTH_LONG).show();

            picUpload = true;
        }
    }

    public void StoringImagetoFireBaseStorage() {

        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrenDate = currentDate.format(getDate.getTime());
        Calendar gettime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(gettime.getTime());
        questionRandomName = saveCurrenDate + saveCurrentTime;

        StorageReference filePath = questionImagesRef.child("Question Images").child(ImageUri.getLastPathSegment() + questionRandomName + ".jpg");


        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(Ask.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    savingQuestionInformation();
                } else {
                    Toast.makeText(Ask.this, "Error occurred:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    public void savingQuestionInformation() {
        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrenDate = currentDate.format(getDate.getTime());
        Calendar gettime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(gettime.getTime());
        questionRandomName = saveCurrenDate + saveCurrentTime;


        UsersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(!picUpload){
                        downloadUrl = "There is no image for this question";
                    }
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    userXps = dataSnapshot.child("points").getValue().toString();
                    HashMap questionMap = new HashMap();
                    questionMap.put("uid", currentUserId);
                    questionMap.put("date", saveCurrenDate);
                    questionMap.put("time", saveCurrentTime);
                    questionMap.put("body", body);
                    questionMap.put("title", title);
                    questionMap.put("questionImage", downloadUrl);
                    questionMap.put("fullname", userFullName);
                    QuestionRef.child(questionRandomName + currentUserId + title).updateChildren(questionMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        openHomeScreen();
                                        Toast.makeText(Ask.this, "Question posted, XPs +1", Toast.LENGTH_SHORT).show();
                                        int newPoint = Integer.parseInt(userXps) + 1;
                                        UsersRef.child(currentUserId).child("points").setValue(newPoint);
                                        loadingBar.dismiss();

                                        return;
                                    } else {
                                        Toast.makeText(Ask.this, "Error Occurred:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void savingQuestionInformationWithNoPic() {


        Calendar getDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrenDate = currentDate.format(getDate.getTime());


        Calendar gettime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(gettime.getTime());
        questionRandomName = saveCurrenDate + saveCurrentTime;


        UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();

                    userXps = dataSnapshot.child("points").getValue().toString();
                    HashMap questionMap = new HashMap();
                    questionMap.put("uid", currentUserId);
                    questionMap.put("date", saveCurrenDate);
                    questionMap.put("time", saveCurrentTime);
                    questionMap.put("body", body);
                    questionMap.put("title", title);
                    questionMap.put("questionImage", "None");
                    questionMap.put("fullname", userFullName);
                    QuestionRef.child(title + " - " + questionRandomName).updateChildren(questionMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        openHomeScreen();
                                        Toast.makeText(Ask.this, "Question posted, XPs +1", Toast.LENGTH_SHORT).show();
                                        /*int newPoint = Integer.parseInt(userXps) +1;
                                        // If this line of code was activated the points updates but it ends in an infinite loop
                                        UsersRef.child(currentUserId).child("points").setValue(newPoint);*/
                                        loadingBar.dismiss();

                                    } else {
                                        Toast.makeText(Ask.this, "Error Occurred:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void backhome() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    public void openHomeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }
}
