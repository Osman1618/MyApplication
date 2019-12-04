package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LeaderBoard extends AppCompatActivity {
    private RecyclerView LeaderBoardRecyclerList;
    private DatabaseReference UsersRef, likesRef;
    private FirebaseDatabase mDataBase;
    private int size;
    private androidx.appcompat.widget.SearchView searchUserLeaderBoard;
    private static final String TAG = "LeaderBoard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        LeaderBoardRecyclerList = (RecyclerView) findViewById(R.id.leader_board_recycler_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        LeaderBoardRecyclerList.setLayoutManager(linearLayoutManager);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDataBase = FirebaseDatabase.getInstance();
        searchUserLeaderBoard = findViewById(R.id.search_user_leader_board);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                size = (int) dataSnapshot.getChildrenCount();
                Log.i(TAG, "Size from Database:  " + size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        searchUserLeaderBoard.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "Query Text after query submit:  " + query);
                if(query.isEmpty()){
                    LeaderBoardDefult();
                } else {
                    LeaderBoardSearch(query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "Query Text before query change:  " + newText);
                if(newText.isEmpty()){
                    LeaderBoardDefult();
                } else {
                    LeaderBoardSearch(newText);
                }                return false;
            }
        });
    }
    private void LeaderBoardSearch(String searchText) {
        final FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(UsersRef.orderByChild("fullname").startAt(searchText).endAt(searchText + "\uf8ff")
                                , User.class).build();
        Log.i(TAG, "Users Ref inside leaderBoardSearch " + UsersRef);
        final FirebaseRecyclerAdapter<User, LeaderBoardViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, LeaderBoardViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position, @NonNull User model) {
                        holder.userNAme.setText(model.getFullname());
                        holder.points.setText(Integer.toString(model.getPoints()));
                        holder.placement.setText("#" + Integer.toString(size - position));
                        Log.i(TAG, "UserName in the LeaderBoard:  " + model.getFullname());
                    }

                    @NonNull
                    @Override
                    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_board_user, parent, false);
                        LeaderBoardViewHolder viewHolder = new LeaderBoardViewHolder(view);
                        return viewHolder;
                    }
                };
        LeaderBoardRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    private void LeaderBoardDefult() {
        final FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(UsersRef.orderByChild("points")
                                , User.class).build();
        Log.i(TAG, "Users Ref inside leaderBoardSearch " + UsersRef);
        final FirebaseRecyclerAdapter<User, LeaderBoardViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, LeaderBoardViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position, @NonNull User model) {
                        holder.userNAme.setText(model.getFullname());
                        holder.points.setText(Integer.toString(model.getPoints()));
                        holder.placement.setText("#" + Integer.toString(size - position));
                        Log.i(TAG, "UserName in the LeaderBoard:  " + model.getFullname());

                    }

                    @NonNull
                    @Override
                    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_board_user, parent, false);
                        LeaderBoardViewHolder viewHolder = new LeaderBoardViewHolder(view);
                        return viewHolder;
                    }
                };
        LeaderBoardRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {

        super.onStart();

        LeaderBoardDefult();

    }

    public static class LeaderBoardViewHolder extends RecyclerView.ViewHolder {
        TextView userNAme, points;
        TextView placement;

        public LeaderBoardViewHolder(@NonNull View itemView) {
            super(itemView);
            userNAme = itemView.findViewById(R.id.user_name_leaderBoard);
            points = itemView.findViewById(R.id.user_points_leaderBoard);
            placement = itemView.findViewById(R.id.user_placement);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_home:
                    openHomeScreen();
                    break;
                case R.id.nav_mail:
                    openMailScreen();
                    break;
                case R.id.nav_leaderBoard:
                    break;
            }
            return true;
        }
    };

    public void openHomeScreen() {
        Intent intent = new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void openMailScreen() {
        Intent intent = new Intent(this, MailScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
