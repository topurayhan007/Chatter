package com.topurayhan.chatter;

import static com.topurayhan.chatter.R.drawable.avatar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ArrayList<User> users;
    HomeAdapter homeAdapter;
    ProgressDialog progressDialog;
    ArrayList<User> sortedUser;

    boolean seen = false;

    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        users = new ArrayList<>();
        sortedUser = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Loading...");
        progressDialog.show();

        chatsButton = findViewById(R.id.chatsButton);
        friendsButton = findViewById(R.id.friendsButton);
        searchButton = findViewById(R.id.searchButton);
        settingsButton = findViewById(R.id.settingsButton);


        FirebaseMessaging.getInstance()
                        .getToken()
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String token) {
                                        //noinspection MismatchedQueryAndUpdateOfCollection
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("token", token);

                                        database.getReference()
                                                .child("users")
                                                .child(mAuth.getUid())
                                                .updateChildren(hashMap);
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
        homeAdapter = new HomeAdapter(this, users);
        binding.homeRecyclerView.setAdapter(homeAdapter);

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                users.clear();
                if (snapshot.getChildrenCount() > 1) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        User user = snapshot1.getValue(User.class);
                        if (!user.getUserId().equals(mAuth.getUid())) {
                            database.getReference().child("users")
                                    .child(mAuth.getUid())
                                    .child("friendList")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                                    String check = String.valueOf(snapshot2.getValue());

                                                    if (check.equals(user.getUserId())) {
                                                        Log.d("YESCheck", user.getUsername());
                                                        users.add(user);
                                                    }

                                                }
                                                Collections.sort(users, new Comparator<User>() {
                                                    @Override
                                                    public int compare(User user, User t1) {
                                                        //return user.getName().compareToIgnoreCase(t1.getName());
                                                        return Long.compare(t1.getLastMsgTime(), user.getLastMsgTime());
                                                    }
                                                });
                                                homeAdapter.notifyDataSetChanged();
                                                binding.homeRecyclerView.smoothScrollToPosition(0);
                                                binding.homeRecyclerView.setVisibility(View.VISIBLE);
                                                progressDialog.dismiss();
                                            }
                                            else{
                                                progressDialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    }
                }
                else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!seen){
                    binding.search.setVisibility(View.VISIBLE);
                    binding.search.clearFocus();
                    binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            fileList(newText);
                            return true;
                        }
                    });
                    seen = true;
                }
                else{
                    binding.search.setVisibility(View.GONE);
                    seen = false;
                }


            }
        });


        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFriendsActivity();
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

    }

    private void fileList(String text) {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        ArrayList<User> filteredList = new ArrayList<>();
        for (User user : users){
            if (user.getName().toLowerCase().contains(text.toLowerCase()) || user.getUsername().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(user);
            }
        }
        if (!filteredList.isEmpty()){
            homeAdapter.setFilteredList(filteredList);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        database.getReference().child("presence")
                .child(mAuth.getUid())
                .setValue("online");
    }

    @Override
    protected void onPause() {
        database.getReference().child("presence")
                .child(mAuth.getUid())
                .setValue("offline");
        super.onPause();
    }

    public void openFriendsActivity(){
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    public void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
