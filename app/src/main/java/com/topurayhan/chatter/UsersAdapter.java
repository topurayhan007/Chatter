package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivitySearchItemBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    Context context;
    ArrayList<User> users;
    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;

    }

    //For search
    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<User> filteredList){
        this.users = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_search_item, parent, false);

        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        holder.binding.friendName.setText(user.getName());
        String username = "@"+user.getUsername();
        holder.binding.username.setText(username);
        String token = user.getToken();
        Picasso.get().load(user.getProfileImage()).into(holder.binding.friendProfilePic);


        //holder.binding.addFriendButton.setVisibility(View.GONE);
//        if(!user.getUserId().equals(mAuth.getUid())){
//            database.getReference().child("users")
//                    .child(mAuth.getUid())
//                    .child("friendList")
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot snapshot2 : snapshot.getChildren()){
//                                String check = String.valueOf(snapshot2.getValue());
//
//                                if(!check.equals(user.getUserId())){
//                                    Log.d("YESCheck", user.getUsername());
//                                    //users.add(user);
//                                    holder.binding.addFriendButton.setVisibility(View.VISIBLE);
//                                }
//
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//        }



        holder.binding.addFriendButton.setVisibility(View.VISIBLE);
        holder.binding.addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendID = user.getUserId();

                database.getReference().child("users")
                        .child(mAuth.getUid())
                        .child("friendList")
                        .child(friendID)
                        .setValue(friendID).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Added to friend list!", Toast.LENGTH_SHORT).show();
                                    holder.binding.addFriendButton.setVisibility(View.GONE);
                                    String message = "Sent you a friend request!";
                                    database.getReference().child("users")
                                                    .child(mAuth.getUid())
                                                    .child("name").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String name = snapshot.getValue(String.class);
                                                    sendNotification(name, message, token);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                }
                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class UsersViewHolder extends RecyclerView.ViewHolder{
        ActivitySearchItemBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ActivitySearchItemBinding.bind(itemView);
        }
    }

    private void sendNotification(String name, String message, String token){
        try {
            RequestQueue queue = Volley.newRequestQueue(context);

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
                    Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, "Notification sending error!", Toast.LENGTH_SHORT).show();
        }
    }
}
