package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
