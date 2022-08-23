package com.topurayhan.chatter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.topurayhan.chatter.databinding.ActivityChattingReceiveItemBinding;
import com.topurayhan.chatter.databinding.ActivityChattingSendItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressWarnings("rawtypes")
public class MessagesAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> messages;

    final int sent = 1;
    final int receive = 2;

    String senderRoom, receiverRoom;


    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == sent){
            View view = LayoutInflater.from(context).inflate(R.layout.activity_chatting_send_item, parent, false);
            return new SentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.activity_chatting_receive_item, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return sent;
        }
        else {
            return receive;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        int[] reactions = new int[]{
                R.drawable.like,
                R.drawable.love,
                R.drawable.care,
                R.drawable.haha,
                R.drawable.wow,
                R.drawable.sad,
                R.drawable.angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (pos < 0){
                return false;
            }
            if (holder.getClass().equals(SentViewHolder.class)){
                SentViewHolder viewHolder = (SentViewHolder)holder;

                if (message.getFeeling() == pos){
                    pos = -1;
                    viewHolder.binding.feeling.setVisibility(View.INVISIBLE);
                }else{
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }
            }
            else {
                ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;

                if (message.getFeeling() == pos){
                    pos = -1;
                    viewHolder.binding.feeling.setVisibility(View.INVISIBLE);
                }else{
                    viewHolder.binding.feeling.setImageResource(reactions[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                }

            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });

        if (holder.getClass().equals(SentViewHolder.class)){
            SentViewHolder viewHolder = (SentViewHolder)holder;
            viewHolder.binding.sendMessage.setText(message.getMessage());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            long time = message.getTimestamp();
            viewHolder.binding.sendMessageTime.setText(dateFormat.format(new Date(time)));

            if(message.getFeeling() >= 0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.sendMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }
        else {
            ReceiveViewHolder viewHolder = (ReceiveViewHolder)holder;
            viewHolder.binding.sendMessage.setText(message.getMessage());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            long time = message.getTimestamp();
            viewHolder.binding.sendMessageTime.setText(dateFormat.format(new Date(time)));

            Log.d("YES", message.getSenderId());
            // received message has senderId, goto database find the user whose ID is equal
            // to the senderId and take that users profileImage and set to friendProfileImage
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.child(message.getSenderId()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()){
                        DataSnapshot snapshot = task.getResult();
                        String profilePic = String.valueOf(snapshot.child("profileImage").getValue());
                        Log.d("YES", profilePic);
                        Picasso.get().load(profilePic).into(viewHolder.binding.friendProfilePic);
                    }
                }
            });



            if(message.getFeeling() >= 0){
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.sendMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view, motionEvent);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Sent Message View Holder
    @SuppressWarnings("InnerClassMayBeStatic")
    public class SentViewHolder extends RecyclerView.ViewHolder{
        ActivityChattingSendItemBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ActivityChattingSendItemBinding.bind(itemView);
        }
    }

    // Receive Message View Holder
    @SuppressWarnings("InnerClassMayBeStatic")
    public class ReceiveViewHolder extends RecyclerView.ViewHolder{
        ActivityChattingReceiveItemBinding binding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ActivityChattingReceiveItemBinding.bind(itemView);
        }
    }
}
