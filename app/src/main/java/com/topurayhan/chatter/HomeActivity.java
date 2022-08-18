package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static LinearLayout chatsButton, friendsButton, searchButton, settingsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
