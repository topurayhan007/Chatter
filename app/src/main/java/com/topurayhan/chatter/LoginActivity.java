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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static EditText emailAddress, password;
    @SuppressLint("StaticFieldLeak")
    static Button loginButton;
    @SuppressLint("StaticFieldLeak")
    static TextView forgotPassword, option1, signUp;

    boolean passwordVisible;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
    ProgressDialog progressDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        option1 = findViewById(R.id.option1);
        signUp = findViewById(R.id.signUp);

        progressDialog = new ProgressDialog(this);

        signUp.setOnClickListener(view -> openSignUpActivity());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Authenticate then openHomeActivity
                openHomeActivity();
//                private void performAuth() {
//                    String email = emailAddress.getText().toString();
//                    String pass = password.getText().toString();
//
//                    if (!email.matches(emailPattern)){
//                        Log.d("TAG", "Enter a valid email!");
//                        emailAddress.setError("Enter a valid email!");
//                    }
//                    else if (pass.isEmpty() || pass.length() < 8){
//                        Log.d("TAG", "Enter a valid email!");
//                        password.setError("Enter a valid password!");
//                    }
//                    else {
//                        progressDialog.setMessage("Please wait...");
//                        progressDialog.setTitle("Logging in");
//                        progressDialog.setCanceledOnTouchOutside(false);
//                        progressDialog.show();
//
//                        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
//                            if (task.isSuccessful()){
//                                progressDialog.dismiss();
//                                // Sign in success, update UI with the signed-in user's information
//                                Log.d("TAG", "signInWithEmail:success");
//                                // updateUI(user);
//                                openHomeActivity();
//                                Toast.makeText(MainActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
//                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                                vibrator.vibrate(15);
//                            }
//                            else{
//                                progressDialog.dismiss();
//                                // If sign in fails, display a message to the user.
//                                Log.w("TAG", "signInWithEmail:failure", task.getException());
//                                Toast.makeText(MainActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
//                                //updateUI(null);
//                            }
//                        });
//                    }
//                }
            }
        });

        password.setOnTouchListener((view, motionEvent) -> {
            final int Right = 2;
            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                if (motionEvent.getRawX() >= password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()){
                    int selection = password.getSelectionEnd();
                    if(passwordVisible){
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);
                        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    }
                    else{
                        password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    password.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }

    public void openSignUpActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }

    public void openHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
