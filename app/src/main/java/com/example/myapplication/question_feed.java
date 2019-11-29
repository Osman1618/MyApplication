package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

public class question_feed extends AppCompatActivity {

    private RecyclerView questionList;
    private ProgressDialog loadingBar;
    private StorageReference questionImagesRef;
    private String body, title;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private DatabaseReference QuestionRef;
    private StorageReference QuestionImageRef;
    private Uri ImageUri;
    private int points;
    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_feed);

        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        questionList = (RecyclerView) findViewById(R.id.all_questions_list);
        questionList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        questionList.setLayoutManager(linearLayoutManager);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.nav_leaderBoard:
                            openLeaderBoardScreen();
                            break;

                        case R.id.nav_mail:
                            openMailScreen();
                            break;
                        case R.id.nav_home:
                            openHome();
                            break;
                    }
                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Question> options = new FirebaseRecyclerOptions.Builder<Question>().setQuery(QuestionRef, Question.class).build();
        FirebaseRecyclerAdapter<Question, QuestionViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Question, QuestionViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position, @NonNull Question model) {

                        holder.username.setText(model.getFullname());
                        holder.questionDate.setText("  " + model.getDate());
                        holder.questionTime.setText("   " + model.getTime());
                        holder.questionTitle.setText(model.getTitle());
                        holder.questionBody.setText(model.getBody());
                        //Picasso.get().load(model.getQuestionImage()).centerCrop().into(holder.questionimage);

                        Picasso.get()
                                .load(model.getQuestionImage())

                                .resize(200,200)
                                .into(holder.questionimage);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String QuestionKey = getRef(position).getKey();
                              //  Toast.makeText(question_feed.this, chose_question_id, Toast.LENGTH_LONG).show();

                                Intent chosePostIntent = new Intent(question_feed.this, click_question_activity.class);
                                chosePostIntent.putExtra("chose_question_id", QuestionKey);
                                startActivity(chosePostIntent);

                            }
                        });
                      /*holder.setFullname(model.getQuestionImage());
                      holder.setBody(model.getBody());
                      holder.setDate(model.getDate());
                      holder.setQuestionImage(model.getQuestionImage());
                      holder.setTime(model.getTime());
                      holder.setTitle(model.getTitle());*/

                    }
                    @NonNull
                    @Override
                    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_questions_layout, parent, false);
                        QuestionViewHolder viewHolder = new QuestionViewHolder(view);
                        return viewHolder;
                    }
                };
        questionList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView username, questionDate, questionTime, questionTitle, questionBody;
        ImageView questionimage;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            username = itemView.findViewById(R.id.question_user_name);
            questionDate = itemView.findViewById(R.id.question_date);
            questionTime = itemView.findViewById(R.id.question_time);
            questionTitle = itemView.findViewById(R.id.question_title);
            questionBody = itemView.findViewById(R.id.question_body);
            questionimage = itemView.findViewById(R.id.question_image);

        }


    }

   /* public static class QuestionViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public QuestionViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setFullname(String fullname)
        {
            TextView username = (TextView) mView.findViewById(R.id.question_user_name);
            username.setText(fullname);
        }


        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.question_time);
            PostTime.setText("    " + time);
        }

        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.question_date);
            PostDate.setText("    " + date);
        }

        public void setBody(String title)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.question_body);
            PostDescription.setText(title);
        }

        public void setTitle(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.question_title);
            PostDescription.setText(description);
        }
        public void setQuestionImage(String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.question_image);
            Picasso.get().load(postimage).into(PostImage);
        }
    }*/



    public void openLeaderBoardScreen() {
        Intent intent = new Intent(this, LeaderBoard.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

    }

    public void openMailScreen() {
        Intent intent = new Intent(this, MailScreen.class);
        startActivity(intent);

    }

    public void openHome() {
        Intent intent = new Intent(this, HomeScreen.class);

        startActivity(intent);

    }


}
