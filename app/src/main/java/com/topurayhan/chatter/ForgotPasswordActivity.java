package com.topurayhan.chatter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.topurayhan.chatter.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends Activity {
    ActivityForgotPasswordBinding binding;
    FirebaseAuth mAuth;
    int error = 0;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                error = 0;
                String emailAddress = binding.emailAddress.getText().toString();

                if (emailAddress.isEmpty()){
                    error++;
                    binding.emailAddress.setError("Enter an email address!");
                }
                else if (!emailAddress.matches(emailPattern)){
                    error++;
                    binding.emailAddress.setError("Enter a valid email!");
                }
                else {
                    if(error == 0) {
                        mAuth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ForgotPasswordActivity.this, "Password reset link has been sent.", Toast.LENGTH_LONG).show();
                                            binding.emailAddress.setText("");
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }
        });
    }

    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
