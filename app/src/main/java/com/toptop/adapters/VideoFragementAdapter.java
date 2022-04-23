package com.toptop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.LoginActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.models.Video;
import com.toptop.utils.RecyclerViewDisabler;

import java.util.List;

public class VideoFragementAdapter extends RecyclerView.Adapter<VideoFragementAdapter.VideoViewHolder> {
	private static final String TAG = "VideoFragementAdapter";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	private List<Video> videos;
	Context context;

	@Override
	public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	public VideoFragementAdapter(List<Video> videos, Context context) {
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

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onBindViewHolder(VideoViewHolder holder, int position) {
		// Set info video
		Video video = videos.get(position);
		holder.txt_username.setText(video.getUsername());
		holder.txt_content.setText(video.getContent());
		holder.txt_num_likes.setText(String.valueOf(video.getTotalLikes()));
		holder.txt_num_comments.setText(String.valueOf(video.getTotalComments()));

		initVideo(holder.videoView, video.getLinkVideo());
		playVideo(holder.videoView, holder.img_pause);

		// Set onClickListener for video
		holder.videoView.setOnClickListener(v -> {
			VideoView videoView = holder.videoView;
			ImageView imgPause = holder.img_pause;

			if (imgPause.getVisibility() == View.VISIBLE) {
				Log.i(TAG, "onClick: play");
				imgPause.setVisibility(View.GONE);
			} else {
				Log.i(TAG, "onClick: pause");
				imgPause.setVisibility(View.VISIBLE);
			}
		});


		// Set onPreparedListener for video
		holder.videoView.setOnPreparedListener(mp -> mp.setLooping(true));

		// Set onClickListener for img_comment
		holder.img_comment.setOnClickListener(v -> {
			// Add layout_comment to MainActivity
			((MainActivity) context)
					.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_comment_container, new CommentFragment(video, context)).commit();
			((MainActivity) context).findViewById(R.id.fragment_comment_container).setVisibility(View.VISIBLE);
		});
	}

	// Find ID corresponding to the name of the resource (in the directory RAW).
	public static int getRawResIdByName(Context context, String resName) {
		String pkgName = context.getPackageName();
		// Return 0 if not found.
		int resID = context.getResources().getIdentifier(resName, "raw", pkgName);

		Log.i(TAG, "Res Name: " + resName + "==> Res ID = " + resID);
		return resID;
	}

	private void initVideo(VideoView videoView, String linkVideo) {
		try {
			videoView.setVideoURI(Uri.parse(linkVideo));
			videoView.requestFocus();
			System.out.println("Init Video");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	public void playVideo(VideoView videoView, ImageView imgPause) {
		Log.i(TAG, "playVideo: ");
		videoView.start();
		imgPause.setVisibility(View.GONE);
	}

	public void pauseVideo(VideoView videoView, ImageView imgPause) {
		Log.i(TAG, "pauseVideo: ");
		videoView.pause();
		imgPause.setVisibility(View.VISIBLE);
	}

	@Override
	public int getItemCount() {
		return videos.size();
	}

	public Long getNumberOfComment(int position) {
		return videos.get(position).getTotalComments();
	}

	static class VideoViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
		VideoView videoView;
		ImageView img_comment, img_pause;

		public VideoViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_username);
			txt_content = itemView.findViewById(R.id.txt_content);
			txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
			txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
			videoView = itemView.findViewById(R.id.videoView);
			img_comment = itemView.findViewById(R.id.img_comment);
			img_pause = itemView.findViewById(R.id.img_pause);
		}
	}

	// Get position of video by video ID
	public int getPosition(String videoID) {
		for (int i = 0; i < videos.size(); i++) {
			if (videos.get(i).getVideo_id().equals(videoID)) {
				return i;
			}
		}
		return -1;
	}
}