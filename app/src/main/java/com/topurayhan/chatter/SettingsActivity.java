package com.topurayhan.chatter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;
    @SuppressLint("StaticFieldLeak")
    static TextView accountSettings, securitySettings, updateProfilePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
    }

    public void openFriendsActivity(){
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openSearchActivity(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openChatsActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openAccountSettingsActivity(){
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openSecuritySettingsActivity(){
        Intent intent = new Intent(this, SecuritySettingsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openUpdateProfilePictureActivity(){
        Intent intent = new Intent(this, UpdateProfilePictureActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
