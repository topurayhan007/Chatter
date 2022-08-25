package com.topurayhan.chatter;

import static com.topurayhan.chatter.R.drawable.avatar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityFriendsBinding;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    ActivityFriendsBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<User> users;
    ArrayList<User> temp;
    FriendsAdapter friendsAdapter;
    ArrayList<String> friends;
    String friendID;

    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        friends = new ArrayList<>();
        users = new ArrayList<>();

        chatsButton = findViewById(R.id.chatsButton);
        friendsButton = findViewById(R.id.friendsButton);
        searchButton = findViewById(R.id.searchButton);
        settingsButton = findViewById(R.id.settingsButton);

        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChatsActivity();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });

        database.getReference().child("users").child(mAuth.getUid()).child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profileImage = snapshot.getValue(String.class);
                    Picasso.get().load(profileImage).into(binding.profilePic);
                    binding.profilePic.setImageURI(Uri.parse(profileImage));
                }
                else {
                    @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(avatar);
                    binding.profilePic.setImageDrawable(d);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendsAdapter = new FriendsAdapter(this, users);
        binding.friendsRecyclerView.setAdapter(friendsAdapter);



        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUserId().equals(mAuth.getUid())){
//                        database.getReference().child("users")
//                            .child(mAuth.getUid())
//                            .child("friendList")
//                            .addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    for (DataSnapshot snapshot2 : snapshot.getChildren()){
//                                        String check = String.valueOf(snapshot2.getValue());
//                                        Log.d("YESc", check);
//                                        if(check.equals(user.getUserId())){
//                                            users.add(user);
//                                            temp.add(user);
//                                            Log.d("YESc", user.toString());
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//                        //
                        users.add(user);
                    }
                }

                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openChatsActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
