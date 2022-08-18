package com.topurayhan.chatter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;

public class SearchActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        chatsButton = findViewById(R.id.chatsButton);
        friendsButton = findViewById(R.id.friendsButton);
        searchButton = findViewById(R.id.searchButton);
        settingsButton = findViewById(R.id.settingsButton);

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFriendsActivity();
            }
        });

        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChatsActivity();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });
    }

    public void openFriendsActivity(){
        Intent intent = new Intent(this, FriendsActivity.class);
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

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
