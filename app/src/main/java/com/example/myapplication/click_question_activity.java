package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class click_question_activity extends AppCompatActivity {

    private String QuestionId, currentUserId, databaseUserID, QuestionBody, QuestionTitle, QuestionImage, CourseCode;
    private ImageView editQuestionImage;
    private TextView editQuestionBody, editQuestionTitle, editQuestionDate, editQuestionTime, editQuestionUserName, editCourseCode;
    private DatabaseReference QuestionRef, mUsersRef;
    private FirebaseAuth mAuth;
    private FloatingActionButton delet, edit;
    private FloatingActionMenu menue_delete_edit;
    private int userXps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_question_activity);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        QuestionId = getIntent().getExtras().get("chose_question_id").toString();

        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions").child(QuestionId);

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        editQuestionImage = findViewById(R.id.edit_question_image);

        editQuestionBody = findViewById(R.id.edit_question_body);
        editQuestionDate = findViewById(R.id.edit_question_date);
        editQuestionTime = findViewById(R.id.edit_question_time);
        editQuestionUserName = findViewById(R.id.edit_question_user_name);
        editQuestionTitle = findViewById(R.id.edit_question_title);
        editCourseCode = findViewById(R.id.edit_course_code);

        delet = findViewById(R.id.delete);
        edit = findViewById(R.id.edit);
        menue_delete_edit = findViewById(R.id.menu_delete_edit);
        menue_delete_edit.setVisibility(View.INVISIBLE);


        QuestionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 if(dataSnapshot.exists()){
                     QuestionBody = dataSnapshot.child("body").getValue().toString();
                     QuestionTitle = dataSnapshot.child("title").getValue().toString();

                     CourseCode = dataSnapshot.child("coursecode").getValue().toString();
                     QuestionImage = dataSnapshot.child("questionImage").getValue().toString();
                     databaseUserID = dataSnapshot.child("uid").getValue().toString();
                     editCourseCode.setText(CourseCode);
                     editQuestionBody.setText(QuestionBody);
                     editQuestionDate.setText(dataSnapshot.child("date").getValue().toString());
                     editQuestionTime.setText(dataSnapshot.child("time").getValue().toString());
                     editQuestionTitle.setText(QuestionTitle);
                     editQuestionUserName.setText(dataSnapshot.child("fullname").getValue().toString());
                     Picasso.get().load(QuestionImage).into(editQuestionImage);
                     if(currentUserId.equals(databaseUserID)){

                         menue_delete_edit.setVisibility(View.VISIBLE);

                     }

                     edit.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             EditChosenQuestion(QuestionBody, QuestionTitle);
                         }
                     });
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteChosenQuestion();
            }
        });


    }

    private void EditChosenQuestion(String QuestionBody, String QuestionTitle){

        AlertDialog.Builder builder = new AlertDialog.Builder(click_question_activity.this);
        builder.setTitle("Edit Post");

        final EditText newQuestionBody = new EditText(click_question_activity.this);
        newQuestionBody.setText(QuestionBody);
        builder.setView(newQuestionBody);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                QuestionRef.child("body").setValue(newQuestionBody.getText().toString());
                Toast.makeText(click_question_activity.this, "Question Updated Successfully", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();

    }
    private void DeleteChosenQuestion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(click_question_activity.this);
        builder.setTitle("Are you sure?");


        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                QuestionRef.removeValue();
                openQuestionFeed();
                mUsersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        userXps = Integer.parseInt(dataSnapshot.child("points").getValue().toString() );
                        mUsersRef.child(currentUserId).child("points").setValue(userXps - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //Remember to decrease points.

                Toast.makeText(click_question_activity.this, "Question deleted Successfully", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void openQuestionFeed(){
        Intent intent = new Intent(click_question_activity.this, question_feed.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


}
