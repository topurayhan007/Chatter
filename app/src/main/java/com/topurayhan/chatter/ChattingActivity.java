package com.topurayhan.chatter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityChattingBinding;

public class ChattingActivity extends AppCompatActivity {
    ActivityChattingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("userName");
        String userId = getIntent().getStringExtra("userId");
        String profileImage = getIntent().getStringExtra("profilePic");
        binding.friendName.setText(name);
        Picasso.get().load(profileImage).into(binding.friendProfilePic);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToChatsActivity();
            }
        });
    }
    public void goBackToChatsActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
