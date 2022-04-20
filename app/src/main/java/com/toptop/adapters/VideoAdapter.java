package com.toptop.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.LoginActivity;
import com.toptop.R;
import com.toptop.models.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private static final String LOG_TAG = "AndroidVideoView";

    private final List<Video> videos;
    Context context;

    @Override
    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        playVideo(holder.videoView, holder.img_pause);
    }

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

        if (position == 0) {
            initVideo(holder.videoView, video.getLinkVideo());
        } else {
            holder.videoView = holder.nextVideoView;
            holder.videoView.setVisibility(View.VISIBLE);
        }
        playVideo(holder.videoView, holder.img_pause);

        holder.videoView.setOnClickListener(v -> {
            VideoView videoView = holder.videoView;
            ImageView imgPause = holder.img_pause;
            if (videoView.isPlaying())
                pauseVideo(videoView, imgPause);
            else
                playVideo(videoView, imgPause);

            if (holder.layout_comment.getVisibility() == View.VISIBLE)
                holder.layout_comment.setVisibility(View.GONE);
        });

        holder.img_comment.setOnClickListener(v -> {
            holder.layout_comment.setVisibility(View.VISIBLE);
        });

        if (position + 1 < videos.size()) {
            Video nextVideo = videos.get(position + 1);
            initVideo(holder.nextVideoView, nextVideo.getLinkVideo());
        }
    }

    // Find ID corresponding to the name of the resource (in the directory RAW).
    public static int getRawResIdByName(Context context, String resName) {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName, "raw", pkgName);

        Log.i(LOG_TAG, "Res Name: " + resName + "==> Res ID = " + resID);
        return resID;
    }

    private void initVideo(VideoView videoView, String linkVideo) {
        linkVideo = "https://vimeo.com/701098493";
        try {
            videoView.setVideoURI(Uri.parse(linkVideo));
            videoView.requestFocus();
            System.out.println("Init Video");
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void playVideo(VideoView videoView, ImageView imgPause) {
        videoView.start();
        imgPause.setVisibility(View.VISIBLE);
    }

    public void pauseVideo(VideoView videoView, ImageView imgPause) {
        videoView.pause();
        imgPause.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
        VideoView videoView, nextVideoView;
        ImageView img_comment, img_pause;
        LinearLayout layout_comment;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_content = itemView.findViewById(R.id.txt_content);
            txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
            txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
            videoView = itemView.findViewById(R.id.videoView);
            nextVideoView = itemView.findViewById(R.id.nextVideoView);
            img_comment = itemView.findViewById(R.id.img_comment);
            img_pause = itemView.findViewById(R.id.img_pause);
            layout_comment = itemView.findViewById(R.id.layout_comment);
        }
    }
}
