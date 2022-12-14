package com.topurayhan.chatter;

import static com.topurayhan.chatter.R.drawable.avatar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;
    @SuppressLint("StaticFieldLeak")
    static TextView accountSettings, securitySettings, updateProfilePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chatsButton = findViewById(R.id.chatsButton);
        friendsButton = findViewById(R.id.friendsButton);
        searchButton = findViewById(R.id.searchButton);
        settingsButton = findViewById(R.id.settingsButton);

        accountSettings = findViewById(R.id.accountSettings);
        securitySettings = findViewById(R.id.securitySettings);
        updateProfilePicture = findViewById(R.id.updateProfilePicture);

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

        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChatsActivity();
            }
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAccountSettingsActivity();
            }
        });

        securitySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSecuritySettingsActivity();
            }
        });

        updateProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateProfilePictureActivity();
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignIn.getClient(
                        SettingsActivity.this,
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();
                openLoginActivity();
                Toast.makeText(SettingsActivity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("users").child(mAuth.getUid()).child("profileImage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profileImage = snapshot.getValue(String.class);
                    Picasso.get().load(profileImage).into(binding.profilePic);
                    //binding.profilePic.setImageURI(Uri.parse(profileImage));
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

        binding.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAboutActivity();
            }
        });
    }

    public void openAboutActivity(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void openFriendsActivity(){
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
        finish();
    }

    public void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }

    public void openChatsActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void openAccountSettingsActivity(){
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
    }

    public void openSecuritySettingsActivity(){
        Intent intent = new Intent(this, SecuritySettingsActivity.class);
        startActivity(intent);
    }

    public void openUpdateProfilePictureActivity(){
        Intent intent = new Intent(this, UpdateProfilePictureActivity.class);
        startActivity(intent);
    }
}
