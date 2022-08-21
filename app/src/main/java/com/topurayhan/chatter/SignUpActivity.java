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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.topurayhan.chatter.databinding.ActivitySignupBinding;

import java.util.HashMap;
import java.util.Map;

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

    boolean passwordVisible;
    boolean passwordVisible2;
    int counter = 0;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
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
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    private void performAuth() {
        String fullName = binding.name.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String email = emailAddress1.getText().toString().trim();
        String password = password1.getText().toString();
        String confirmPassword = binding.password2.getText().toString();

        // Create a reference to the cities collection
        CollectionReference usersRef = db.collection("users");

        Query usernameCheck = null;

        if (db.collection("users") != null){
            usernameCheck = usersRef.whereEqualTo("username", username);
            usernameCheck.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        counter = task.getResult().size();

                    }
                }
            });
        }
        Log.d("YES", String.valueOf(counter));
        if (fullName.isEmpty()){
            binding.name.setError("Enter a name!");
        }
        //Username cannot be duplicate
        else if (username.isEmpty()){
            binding.username.setError("Enter a username!");
        }
        else if (counter != 0){
            binding.username.setError("Username already taken!");
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

    private void updateDatabase(String userID, String fullName, String username, String email) {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> friendList = new HashMap<>();
        friendList.put("friendID", null);
//        Map<String, Object> chat = new HashMap<>();
//        chat.put("friendID", null);
//        chat.put("messages", null);


        user.put("fullName", fullName);
        user.put("username", username);
        user.put("email", email);
        user.put("profilePic", null);
        user.put("friendList", friendList);

        // Add a new document with a generated ID
        db.collection("users")
            .document(userID).set(user)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    openLoginActivity();
                    Toast.makeText(SignUpActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error adding document", e);
                }
            });
    }
}
