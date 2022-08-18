package com.topurayhan.chatter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;

public class FriendsActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

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
    }

    public void openChatsActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
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

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
