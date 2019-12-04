package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class question_feed extends AppCompatActivity {

    private RecyclerView questionList;
    private ProgressDialog loadingBar;
    private StorageReference questionImagesRef;
    private String body, title;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, likesRef;
    private DatabaseReference QuestionRef;
    private StorageReference QuestionImageRef;
    private Uri ImageUri;
    private int points;
    private ImageButton newQuestion;
    private static final String TAG = "question_feed";

    String currentUserId;

    Boolean likeChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_feed);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        questionList = findViewById(R.id.all_questions_list);
        questionList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        questionList.setLayoutManager(linearLayoutManager);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        newQuestion = findViewById(R.id.new_question_icon);

        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAskScreen();
            }
        });
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

    @NonNull
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Question> options =
                new FirebaseRecyclerOptions.Builder<Question>()
                        .setQuery(QuestionRef, Question.class).build();
        FirebaseRecyclerAdapter<Question, QuestionViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Question, QuestionViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull QuestionViewHolder holder, final int position, @NonNull Question model) {
                        final String QuestionKey;
                        QuestionKey = getRef(position).getKey();
                        holder.username.setText(model.getFullname());
                        holder.questionDate.setText("  " + model.getDate());
                        holder.questionTime.setText("  " + model.getTime());
                        holder.questionTitle.setText(model.getTitle());
                        holder.questionBody.setText(model.getBody());
                        holder.setLikeButtonStatus(QuestionKey);
                       //Picasso.get().load(model.getQuestionImage()).into(holder.questionimage);

                        Picasso.get().load(model.getQuestionImage()).fit().centerInside().into(holder.questionimage);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //  Toast.makeText(question_feed.this, chose_question_id, Toast.LENGTH_LONG).show();
                                Intent chosePostIntent = new Intent(question_feed.this, click_question_activity.class);
                                chosePostIntent.putExtra("chose_question_id", QuestionKey);
                                startActivity(chosePostIntent);
                            }
                        });
                        holder.commentQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentsIntent = new Intent(question_feed.this, CommentsActivity.class);
                                commentsIntent.putExtra("chose_question_id", QuestionKey);
                                startActivity(commentsIntent);
                            }
                        });
                        holder.likeQuesionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likeChecker = true;
                                likesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (likeChecker.equals(true)) {
                                            if (dataSnapshot.child(QuestionKey).hasChild(currentUserId)) {
                                                likesRef.child(QuestionKey).child(currentUserId).removeValue();
                                                likeChecker = false;
                                            } else {
                                                likesRef.child(QuestionKey).child(currentUserId).setValue(true);
                                                likeChecker = false;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        });
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
        ImageButton likeQuesionButton, commentQuestion;
        TextView displayLikes;
        int countLikes;
        String currentUserId;
        DatabaseReference likesRef;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            likeQuesionButton = (ImageButton) mView.findViewById(R.id.like_button);
            commentQuestion = (ImageButton) mView.findViewById(R.id.comment_button);
            displayLikes = mView.findViewById(R.id.display_likes);


            username = mView.findViewById(R.id.question_user_name);
            questionDate = mView.findViewById(R.id.question_date);
            questionTime = mView.findViewById(R.id.question_time);
            questionTitle = mView.findViewById(R.id.question_title);
            questionBody = mView.findViewById(R.id.question_body);
            questionimage = mView.findViewById(R.id.display_question_image);
            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setLikeButtonStatus(final String QuestionKey) {
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(QuestionKey).hasChild(currentUserId)) {
                        countLikes = (int) dataSnapshot.child(QuestionKey).getChildrenCount();
                        likeQuesionButton.setImageResource(R.drawable.ic_exposure_plus_1_green_chosen);
                        displayLikes.setText((Integer.toString(countLikes+1)) + " wants to know the answer");
                    } else {
                        countLikes = (int) dataSnapshot.child(QuestionKey).getChildrenCount();
                        likeQuesionButton.setImageResource(R.drawable.ic_exposure_plus_1_not_chosen);
                        displayLikes.setText((Integer.toString(countLikes+1)) + " wants to know the answer");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
    public void openAskScreen(){
        Intent intent = new Intent(this, Ask.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
