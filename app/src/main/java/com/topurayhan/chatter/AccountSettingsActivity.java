package com.topurayhan.chatter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.topurayhan.chatter.databinding.ActivityAccountSettingsBinding;

import java.util.HashMap;

public class AccountSettingsActivity extends AppCompatActivity {
    ActivityAccountSettingsBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    static AppCompatImageView backButton;
    int error = 0;
    //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backButton = findViewById(R.id.backButton);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToSettingsActivity();
            }
        });

        binding.confirmButton.setVisibility(View.INVISIBLE);

        database.getReference().child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    binding.name.setText(name);
                    binding.username.setText(username);
                    binding.emailAddress1.setText(email);

                    binding.name.setEnabled(false);
                    binding.username.setEnabled(false);
                    binding.emailAddress1.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.confirmButton.setVisibility(View.VISIBLE);
                binding.name.setEnabled(true);
                binding.username.setEnabled(true);
                //binding.emailAddress1.setEnabled(true);
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performAuth();
            }

        });
    }

    public void goBackToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void performAuth(){
        String name = binding.name.getText().toString();
        String username = binding.username.getText().toString();
        //String email = binding.emailAddress1.getText().toString();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                error = 0;
                if (name.isEmpty()) {
                    binding.name.setError("Enter a name!");
                    error++;
                } else if (username.isEmpty()) {
                    binding.username.setError("Enter a username!");
                    error++;
                }
                //Username cannot be duplicate
                else if (dataSnapshot.exists()) {
                    binding.username.setError("Username already taken!");
                    error++;
                }
//                else if (!email.matches(emailPattern) || email.isEmpty()) {
//                    Log.d("TAG", "Enter a valid email!");
//                    binding.emailAddress1.setError("Enter a valid email!");
//                    error++;
//                }
                else {
                    if (error == 0) {
                        updateDatabase(name, username);
                        //updateDatabase(name, username, email);
//                        mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("TAG", "User email address updated.");
//
//                                }
//                                else{
//                                    Toast.makeText(AccountSettingsActivity.this, "Email couldn't be updated!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateDatabase(String name, String username) {
        HashMap<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("username", username);
        //user.put("email", email);

        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database.getReference().child("users").child(mAuth.getUid()).updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.name.setEnabled(false);
                    binding.username.setEnabled(false);
                    binding.emailAddress1.setEnabled(false);
                    binding.confirmButton.setVisibility(View.INVISIBLE);

                    Toast.makeText(AccountSettingsActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(15);
                    progressDialog.dismiss();

                } else {
                    Toast.makeText(AccountSettingsActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}