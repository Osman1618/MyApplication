package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class MyQuestions extends AppCompatActivity {


    private RecyclerView MyQuestionsList;
    private ProgressDialog loadingBar;
    private String body, title;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, likesRef;
    private DatabaseReference QuestionRef;
    private StorageReference QuestionImageRef;
    private Uri ImageUri;
    private int points;
    private SearchView searchViewMyQuestions;

    String currentUserId;

    Boolean likeChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        MyQuestionsList = findViewById(R.id.all_my_questions_list);
        MyQuestionsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        MyQuestionsList.setLayoutManager(linearLayoutManager);
        QuestionRef = FirebaseDatabase.getInstance().getReference().child("Questions");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        searchViewMyQuestions = findViewById(R.id.search_bar_my_question_feed);


    }
    @Override
    protected void onStart() {
        super.onStart();
        MyQuestionFeedDefault();
    }
    private void MyQuestionFeedDefault(){
        FirebaseRecyclerOptions<Question> options =
                new FirebaseRecyclerOptions.Builder<Question>()
                        .setQuery(QuestionRef.orderByChild("uid").equalTo(currentUserId)
                                , Question.class).build();
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
                        holder.corseCode.setText(model.getCoursecode());
                        //Picasso.get().load(model.getQuestionImage()).into(holder.questionimage);

                        Picasso.get().load(model.getQuestionImage()).fit().centerInside().into(holder.questionimage);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //  Toast.makeText(question_feed.this, chose_question_id, Toast.LENGTH_LONG).show();
                                Intent chosePostIntent = new Intent(MyQuestions.this, click_question_activity.class);
                                chosePostIntent.putExtra("chose_question_id", QuestionKey);
                                startActivity(chosePostIntent);
                            }
                        });
                        holder.commentQuestion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent commentsIntent = new Intent(MyQuestions.this, CommentsActivity.class);
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
        MyQuestionsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView username, questionDate, questionTime, questionTitle, questionBody, corseCode;
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
            corseCode = mView.findViewById(R.id.course_Code);
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
