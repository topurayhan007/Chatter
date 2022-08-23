package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityHomeItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("rawtypes")
public class HomeAdapter extends RecyclerView.Adapter {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    Context context;
    ArrayList<User> users;
    public HomeAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_home_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getClass().equals(HomeViewHolder.class)){
            HomeViewHolder viewHolder = (HomeViewHolder) holder;

            User user = users.get(position);
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();

            String senderId = mAuth.getUid();
            String senderRoom = senderId + user.getUserId();
            database.getReference().child("chats")
                    .child(senderRoom).addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                long time = snapshot.child("lastTime").getValue(Long.class);
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                                ((HomeViewHolder) holder).binding.friendLastMsg.setText(lastMsg);
                                ((HomeViewHolder) holder).binding.friendLastMsgTime.setText(dateFormat.format(new Date(time)));

                            }
                            else {
                                ((HomeViewHolder) holder).binding.friendLastMsg.setText("Tap to chat");
                                ((HomeViewHolder) holder).binding.friendLastMsgTime.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

            ((HomeViewHolder) holder).binding.friendName.setText(user.getName());
            Picasso.get().load(user.getProfileImage()).into(((HomeViewHolder) holder).binding.friendProfilePic);
            ((HomeViewHolder) holder).binding.friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra("userName", user.getName());
                    intent.putExtra("profilePic", user.getProfileImage());
                    intent.putExtra("userId", user.getUserId());
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class HomeViewHolder extends RecyclerView.ViewHolder {
        ActivityHomeItemBinding binding;
        public HomeViewHolder(View view) {
            super(view);
            binding = ActivityHomeItemBinding.bind(view);
        }
    }
}
