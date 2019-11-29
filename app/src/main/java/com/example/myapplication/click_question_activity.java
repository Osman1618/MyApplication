package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class click_question_activity extends AppCompatActivity {

    private String QuestionId;
    private ImageView editQuestionImage;
    private Button editQuestion, deleteQuestion;
    private TextView editQuestionBody, editQuestionTitle, editQuestionDate, editQuestionTime, editQuestionUserName;
    private DatabaseReference QuestionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_question_activity);


        QuestionId = getIntent().getExtras().get("chose_question_id").toString();

        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions").child(QuestionId);

        editQuestionImage = findViewById(R.id.edit_question_image);
        editQuestion = findViewById(R.id.edit_question);
        deleteQuestion = findViewById(R.id.delete_question);
        editQuestionBody = findViewById(R.id.edit_question_body);
        editQuestionDate = findViewById(R.id.edit_question_date);
        editQuestionTime = findViewById(R.id.edit_question_time);
        editQuestionUserName = findViewById(R.id.edit_question_user_name);
        editQuestionTitle = findViewById(R.id.edit_question_title);


        QuestionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // String QuestionBody = dataSnapshot.child("body").getValue().toString();
                  String QuestionImage = dataSnapshot.child("questionImage").getValue().toString();

                editQuestionBody.setText(dataSnapshot.child("body").getValue().toString());
                editQuestionDate.setText(dataSnapshot.child("date").getValue().toString());
                editQuestionTime.setText(dataSnapshot.child("time").getValue().toString());
                editQuestionTitle.setText(dataSnapshot.child("title").getValue().toString());
                editQuestionUserName.setText(dataSnapshot.child("fullname").getValue().toString());

                  Picasso.get().load(QuestionImage).into(editQuestionImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
