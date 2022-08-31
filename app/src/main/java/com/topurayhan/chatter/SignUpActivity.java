package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.topurayhan.chatter.databinding.ActivitySignupBinding;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignupBinding binding;

    @SuppressLint("StaticFieldLeak")
    static EditText name, emailAddress1, password1;
    @SuppressLint("StaticFieldLeak")
    static Button signupButton;
    @SuppressLint("StaticFieldLeak")
    static TextView login;

    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseDatabase database;

    boolean passwordVisible;
    boolean passwordVisible2;
    int error = 0; boolean count = false;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        password1 = findViewById(R.id.password1);
        name = findViewById(R.id.name);
        emailAddress1 = findViewById(R.id.emailAddress1);
        signupButton = findViewById(R.id.signupButton);
        name = findViewById(R.id.name);
        login = findViewById(R.id.login);

        password1.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= password1.getRight() - password1.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = password1.getSelectionEnd();
                    if(passwordVisible){
                        password1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    }
                    else{
                        password1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        password1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    password1.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        binding.password2.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= binding.password2.getRight() - binding.password2.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = binding.password2.getSelectionEnd();
                    if(passwordVisible2){
                        binding.password2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        binding.password2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible2 = false;
                    }
                    else{
                        binding.password2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        binding.password2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible2 = true;
                    }
                    binding.password2.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        login.setOnClickListener(view -> openLoginActivity());
        signupButton.setOnClickListener(view -> performAuth());
    }

    public void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //finishAffinity();
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    private void performAuth() {
        String fullName = binding.name.getText().toString();
        String username = binding.username.getText().toString();
        String email = emailAddress1.getText().toString();
        String password = password1.getText().toString();
        String confirmPassword = binding.password2.getText().toString();



        // Create a reference to the cities collection
        if (database == null) {
            if (fullName.isEmpty()){
                binding.name.setError("Enter a name!");
            }

            else if (username.isEmpty()){
                binding.username.setError("Enter a username!");
            }

            else if (!email.matches(emailPattern) || email.isEmpty()){
                Log.d("TAG", "Enter a valid email!");
                emailAddress1.setError("Enter a valid email!");
            }
            else if (password.isEmpty() || password.length() < 8){
                Log.d("TAG", "Enter a valid email!");
                password1.setError("Password should contain at least 8 characters!");
            }
            else if (confirmPassword.isEmpty() || confirmPassword.length() < 8){
                Log.d("TAG", "Enter a valid email!");
                binding.password2.setError("Password should contain at least 8 characters!");
            }
            else if (confirmPassword.length() >= 8 && password.length() >= 8 && !confirmPassword.matches(password)){
                Log.d("TAG", "Passwords doesn't match!");
                binding.password2.setError("Passwords doesn't match!");
            }
            else {
                progressDialog.setMessage("Please wait...");
                progressDialog.setTitle("Registration");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userID = user.getUid();
                        Log.d("YES", "YES");
                        updateDatabase(userID, fullName, username, email);
                    }
                    else{
                        progressDialog.dismiss();
                        // If sign in fails, display a message to the user.
                        Log.d("YES", "NO");
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Registration failed.",Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                });
            }
        }
        else {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                    error = 0;
                    if (fullName.isEmpty()){
                        binding.name.setError("Enter a name!");
                    }

                    else if (username.isEmpty()){
                        binding.username.setError("Enter a username!");
                    }
                    //Username cannot be duplicate
                    else if (dataSnapshot.exists()){
                        binding.username.setError("Username already taken!");
                        error++;
                    }
                    else if (!email.matches(emailPattern) || email.isEmpty()){
                        Log.d("TAG", "Enter a valid email!");
                        emailAddress1.setError("Enter a valid email!");
                    }
                    else if (password.isEmpty() || password.length() < 8){
                        Log.d("TAG", "Enter a valid email!");
                        password1.setError("Password should contain at least 8 characters!");
                    }
                    else if (confirmPassword.isEmpty() || confirmPassword.length() < 8){
                        Log.d("TAG", "Enter a valid email!");
                        binding.password2.setError("Password should contain at least 8 characters!");
                    }
                    else if (confirmPassword.length() >= 8 && password.length() >= 8 && !confirmPassword.matches(password)){
                        Log.d("TAG", "Passwords doesn't match!");
                        binding.password2.setError("Passwords doesn't match!");
                    }
                    else {
                        if (error == 0){
                            progressDialog.setMessage("Please wait...");
                            progressDialog.setTitle("Registration");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()){
                                    progressDialog.dismiss();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String userID = user.getUid();
                                    Log.d("YES", "YESCheck");
                                    updateDatabase(userID, fullName, username, email);
                                }
                                else{
                                    progressDialog.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Log.d("YES", "NO");
                                    Log.w("TAG", "createUserWithEmail:failure", task2.getException());
                                    Toast.makeText(SignUpActivity.this, "Registration failed.",Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            });
                        }
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        //DatabaseReference usersRef = database.getReference().child("users");


    }

    private void updateDatabase(String userID, String fullName, String username, String email) {
        Log.d("YES", "YESup");
        // Create a new user
        String profileImage = null;
        HashMap<String, Object> friend = new HashMap<>();

        String friendList = null;
        User user = new User(userID, fullName, username, email, profileImage);
        Log.d("YES", String.valueOf(user));

        database.getReference()
                .child("users")
                .child(userID)
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("YES", "YESLo");
                        openLoginActivity();
                        Toast.makeText(SignUpActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error adding document", e);
                    Toast.makeText(SignUpActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                }
            });

    }
}
