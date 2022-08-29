package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityChattingBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {
    ActivityChattingBinding binding;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messages;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    String senderRoom, receiverRoom, senderID, receiverID, senderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChattingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Sending Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        messages = new ArrayList<>();

        String name = getIntent().getStringExtra("userName");
        String profileImage = getIntent().getStringExtra("profilePic");
        String token = getIntent().getStringExtra("token");
        //senderName = getIntent().getStringExtra("senderName");




        binding.friendName.setText(name);
        Picasso.get().load(profileImage).into(binding.friendProfilePic);


        receiverID = getIntent().getStringExtra("userId");
        senderID = FirebaseAuth.getInstance().getUid();

        database.getReference().child("presence").child(receiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    if (status.equals("offline")){
                        binding.status.setText(status);
                        binding.status.setTextColor(getResources().getColor(R.color.grey));
                        binding.status.setVisibility(View.VISIBLE);
                    }
                    else if (status.equals("typing...")){
                        binding.status.setText(status);
                        binding.status.setTextColor(getResources().getColor(R.color.blue));
                        binding.status.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.status.setText(status);
                        binding.status.setTextColor(getResources().getColor(R.color.green));
                        binding.status.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderID + receiverID;
        receiverRoom = receiverID + senderID;

        messagesAdapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        binding.chattingRecyclerView.setAdapter(messagesAdapter);

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();

                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageInput = binding.messageInput.getText().toString();
                Date date = new Date();
                Message message = new Message(messageInput, senderID, date.getTime());

                binding.messageInput.setText("");
                String randomKey = database.getReference().push().getKey();
                String msg;
                //noinspection MismatchedQueryAndUpdateOfCollection
                HashMap<String, Object> lastMsgObj = new HashMap<>();
                if (message.getMessage().length() > 30){
                    msg = message.getMessage().substring(0, 30) + ".... ";
                    lastMsgObj.put("lastMsg", msg);
                }
                else{
                    lastMsgObj.put("lastMsg", message.getMessage());
                }

                lastMsgObj.put("lastMsgTime", date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //sendNotification(name, message.getMessage(), token);
                                            }
                                        });
                            }
                        });
                binding.chattingRecyclerView.smoothScrollToPosition(messagesAdapter.getItemCount()+100);
            }

        });


        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.sendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 50);

            }
        });

        final Handler handler = new Handler();


        binding.messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence").child(senderID).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }
            final Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderID).setValue("online");
                }
            };
        });

    }


    void sendNotification(String name, String message, String token){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //Toast.makeText(ChattingActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChattingActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> hashMap = new HashMap<>();
                    // Firebase Cloud Messaging Server Key
                    String key = "Key=AAAAJh5_FCU:APA91bF8PQ9b0qHcYlO2BbBJtQXRRS9HsG9SQKVZ6sQcPtYfRevO8w8Dis4ZavAZDtjYMOxyDPCvTXQQj4WFUEea6G240W9svGHV0cjjqUBRSf9GLAaBWY5OZwFZj-ZoSisygpipgwXF";
                    hashMap.put("Authorization", key);
                    hashMap.put("Content-Type", "application/json");
                    return hashMap;
                }
            };

            queue.add(request);
        }
        catch (Exception ex){
            Toast.makeText(this, "Notification sending error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 50){
            if (data != null){
                if (data.getData() != null){
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference()
                            .child("chats")
                            .child(calendar.getTimeInMillis() + "");

                    progressDialog.show();

                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();

                                        String messageInput = binding.messageInput.getText().toString();
                                        Date date = new Date();
                                        Message message = new Message(messageInput, senderID, date.getTime());
                                        message.setMessage("Photo");
                                        message.setImageUrl(filepath);

                                        binding.messageInput.setText("");
                                        String randomKey = database.getReference().push().getKey();

                                        //noinspection MismatchedQueryAndUpdateOfCollection
                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        database.getReference().child("chats")
                                                                .child(receiverRoom)
                                                                .child("messages")
                                                                .child(randomKey)
                                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        database.getReference().child("presence")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue("online");

    }
    @Override
    protected void onPause() {
        database.getReference().child("presence")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue("offline");
        super.onPause();
    }

    public void goBackToChatsActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(15);
    }
}
