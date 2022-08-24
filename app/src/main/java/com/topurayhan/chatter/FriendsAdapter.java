package com.topurayhan.chatter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityFriendsItemBinding;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    Context context;
    ArrayList<User> users;

    public FriendsAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FriendsAdapter.FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_friends_item, parent, false);
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.FriendsViewHolder holder, int position) {
        User user = users.get(position);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        holder.binding.friendName.setText(user.getName());
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
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class FriendsViewHolder extends RecyclerView.ViewHolder {
        ActivityFriendsItemBinding binding;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ActivityFriendsItemBinding.bind(itemView);
        }
    }
}
