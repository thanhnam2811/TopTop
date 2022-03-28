package com.toptop.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.LoginActivity;
import com.toptop.R;
import com.toptop.models.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private final List<Video> videos;
    Context context;

    public VideoAdapter(List<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_layout, parent, false);

        ImageView imgFollow = view.findViewById(R.id.img_follow);
        imgFollow.setOnClickListener(v -> {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        });
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.txt_username.setText(video.getUsername());
        holder.txt_content.setText(video.getContent());
        holder.txt_num_likes.setText(String.valueOf(video.getTotalLikes()));
        holder.txt_num_comments.setText(String.valueOf(video.getTotalComments()));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
        public VideoViewHolder(View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_content = itemView.findViewById(R.id.txt_content);
            txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
            txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
        }
    }
}
