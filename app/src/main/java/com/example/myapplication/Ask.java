package com.example.myapplication;
/*
Course Code: for tags.
XPs : updates.

 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
    private Boolean picUpload;

    final static int Gallery_pick = 1;

    private ProgressDialog loadingBar;
    private StorageReference questionImagesRef;
    private String body, title;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference QuestionRef;
    private StorageReference QuestionImageRef;
    private Uri ImageUri;

    private String saveCurrenDate, saveCurrentTime, questionRandomName, downloadUrl;
    String currentUserId;
    String questionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        QuestionImageRef = FirebaseStorage.getInstance().getReference().child("QuestionImages");

        picUpload = false;
        loadingBar = new ProgressDialog(this);
        backHome = (ImageButton) findViewById(R.id.back_home);
        upload = findViewById(R.id.Upload_Picture);
        submit = findViewById(R.id.submit);
        questionBody = findViewById(R.id.question);
        questionTitle = findViewById(R.id.question_title);
        anonymous = findViewById(R.id.anonymous);

        questionId = questionTitle.getText().toString();
        questionImagesRef = FirebaseStorage.getInstance().getReference();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picUpload = true;
                openGallery();
            }


        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backhome();
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

        } else if (picUpload ) {
            loadingBar.setTitle("Question submit");
            loadingBar.setMessage("Your question is being posted");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImagetoFireBaseStorage();
        }
        else {
            loadingBar.setTitle("Question submit");
            loadingBar.setMessage("Your question is being posted");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            savingQuestionInformationWithNoPic();

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

        UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();

                    HashMap questionMap = new HashMap();
                    questionMap.put("uid", currentUserId);
                    questionMap.put("date", saveCurrenDate);
                    questionMap.put("time", saveCurrentTime);
                    questionMap.put("body", body);
                    questionMap.put("title", title);
                    questionMap.put("questionImage", downloadUrl);
                    questionMap.put("fullname", userFullName);

                    QuestionRef.child(title + " - " + questionRandomName).updateChildren(questionMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                openHomeScreen();
                                Toast.makeText(Ask.this, "Question posted, XPs +1", Toast.LENGTH_SHORT).show();

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
                                    if(task.isSuccessful()){
                                        openHomeScreen();
                                        Toast.makeText(Ask.this, "Question posted, XPs +1", Toast.LENGTH_SHORT).show();

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

            Toast.makeText(Ask.this, "Uploaded", Toast.LENGTH_SHORT).show();
           /* CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);*/

        }
        /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (requestCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference filePath = QuestionImageRef.child(questionId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                            QuestionRef.child("question image").setValue(downloadUrl).addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Ask.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(Ask.this, "Error occurred:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(Ask.this, "Error occurred:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else {
                Toast.makeText(Ask.this, "Error, try again", Toast.LENGTH_SHORT).show();

            }
        }*/
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
}
