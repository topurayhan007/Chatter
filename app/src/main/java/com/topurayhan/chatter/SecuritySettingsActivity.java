package com.topurayhan.chatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class SecuritySettingsActivity extends AppCompatActivity {
    static AppCompatImageView backButton;
    @SuppressLint("StaticFieldLeak")
    static EditText currentPassword, newPassword, confirmPassword;

    boolean passwordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_settings);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        currentPassword.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= currentPassword.getRight() - currentPassword.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = currentPassword.getSelectionEnd();
                    if(passwordVisible){
                        currentPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        currentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    }
                    else{
                        currentPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        currentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    currentPassword.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        newPassword.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= newPassword.getRight() - newPassword.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = newPassword.getSelectionEnd();
                    if(passwordVisible){
                        newPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    }
                    else{
                        newPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    newPassword.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        confirmPassword.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= confirmPassword.getRight() - confirmPassword.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = confirmPassword.getSelectionEnd();
                    if(passwordVisible){
                        confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    }
                    else{
                        confirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    confirmPassword.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToSettingsActivity();
            }
        });
    }

    public void goBackToSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
