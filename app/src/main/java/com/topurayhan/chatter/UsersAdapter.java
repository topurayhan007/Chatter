package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivitySearchItemBinding;

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
        holder.binding.username.setText(user.getUsername());
        Picasso.get().load(user.getProfileImage()).into(holder.binding.friendProfilePic);
        holder.binding.friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("userName", user.getName());
                intent.putExtra("profilePic", user.getProfileImage());
                intent.putExtra("userId", user.getUserId());
                context.startActivity(intent);
            }
        });

        holder.binding.addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> friendId = null;
                friendId.add(user.getUserId());
                user.setFriendList(friendId);
                Log.d("YES", friendId.toString());
                database.getReference().child("users").child(mAuth.getUid()).child("friendList").setValue(friendId).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Added to friend list!", Toast.LENGTH_SHORT).show();
                            holder.binding.addFriendButton.setVisibility(View.INVISIBLE);
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
}
