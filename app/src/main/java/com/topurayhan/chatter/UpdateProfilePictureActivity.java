package com.topurayhan.chatter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityUpdateProfilePictureBinding;

import java.util.HashMap;

public class UpdateProfilePictureActivity extends AppCompatActivity{
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    Uri profileImage;
    ProgressDialog progressDialog, progressDialog2;

    ActivityUpdateProfilePictureBinding binding;
    static AppCompatImageView backButton;
    private boolean selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfilePictureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backButton = findViewById(R.id.backButton);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);

        binding.confirmButton.setVisibility(View.INVISIBLE);

        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        database.getReference().child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profileImage = snapshot.child("profileImage").getValue(String.class);
                    Picasso.get().load(profileImage).into(binding.profilePic);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String name = snapshot.child("name").getValue(String.class);

                    //binding.profilePic.setImageURI();
                    binding.name.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backButton.setOnClickListener(view -> goBackToSettingsActivity());

        binding.uploadPhotoButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 50);
            selected = true;


        });
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (profileImage != null) {
                        progressDialog2.setTitle("Uploading");
                        progressDialog2.setMessage("Please wait...");
                        progressDialog2.setCanceledOnTouchOutside(false);
                        progressDialog2.show();

                        StorageReference reference = storage.getReference().child("Profiles").child(mAuth.getUid());
                        reference.putFile(profileImage).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUri = uri.toString().trim() + ".jpg";
                                    Log.d("YESUri", imageUri);
                                    String userId = mAuth.getUid();
                                    HashMap<String, Object> hashMap = new HashMap<>();

                                    hashMap.put("profileImage", imageUri);
                                    Log.d("YES", "YES");

                                    database.getReference().child("users").child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                              Toast.makeText(UpdateProfilePictureActivity.this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show();
                                              Picasso.get().load(imageUri).into(binding.profilePic);
                                              Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                              vibrator.vibrate(15);
                                              progressDialog2.dismiss();
                                              binding.confirmButton.setVisibility(View.INVISIBLE);
                                          }
                                      }
                                    );
                                });
                            }
                        });
                    }
                else {
                    Toast.makeText(UpdateProfilePictureActivity.this, "Please select a photo!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if(data.getData() != null){
                binding.profilePic.setImageURI(data.getData());
                profileImage = data.getData();
                binding.confirmButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void goBackToSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
