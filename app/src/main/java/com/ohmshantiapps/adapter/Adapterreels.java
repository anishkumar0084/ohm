package com.ohmshantiapps.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ohmshantiapps.R;
import com.ohmshantiapps.model.ModelPost;

import java.util.List;

public class Adapterreels extends RecyclerView.Adapter<Adapterreels.ReelViewHolder> {

    private Context context;
    private List<ModelPost> reelList; // Using the reels class

    public  Adapterreels (Context context, List<ModelPost> reelList) {
        this.context = context;
        this.reelList = reelList;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reels, parent, false);
        return new ReelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        ModelPost reel = reelList.get(position); // Using the reels class

        holder.videoView.setVideoURI(Uri.parse(reel.getVine()));
        holder.videoView.start();

//        holder.userProfilePhoto.setImageResource(reel.getDp());
//        holder.usedAudio.setImageResource(reel.getUsedAudio());
        holder.userName.setText(reel.getName());
        holder.description.setText(reel.getText());

        holder.likeButton.setOnClickListener(v -> {
            // Handle like button click
        });

        holder.commentButton.setOnClickListener(v -> {
            // Handle comment button click
        });

        holder.shareButton.setOnClickListener(v -> {
            // Handle share button click
        });
    }

    @Override
    public int getItemCount() {
        return reelList.size();
    }

    static class ReelViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        ImageView userProfilePhoto, usedAudio;
        TextView userName, description;
        ImageButton likeButton, commentButton, shareButton;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            userProfilePhoto = itemView.findViewById(R.id.user_profile_photo);
            usedAudio = itemView.findViewById(R.id.used_audio);
            userName = itemView.findViewById(R.id.user_name);
            description = itemView.findViewById(R.id.description);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            shareButton = itemView.findViewById(R.id.share_button);
        }
    }
}
