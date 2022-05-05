package com.toptop.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.LoginActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.VideoFirebase;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class VideoFragementAdapter extends RecyclerView.Adapter<VideoFragementAdapter.VideoViewHolder> {
	private static final String TAG = "VideoFragementAdapter";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();

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
	public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
		// Check video is exist or not
		if (videos.get(position) == null)
			return;

		// Set info video
		Video video = videos.get(position);
		holder.txt_username.setText(video.getUsername());
		holder.txt_content.setText(video.getContent());
		holder.txt_num_likes.setText(String.valueOf(video.getNumLikes()));
		holder.txt_num_comments.setText(String.valueOf(video.getNumComments()));

		// Check if video is liked or not
		if (video.isLiked()) {
			holder.img_like.setImageResource(R.drawable.ic_liked);
		}

		initVideo(holder.videoView, video);
		playVideo(holder.videoView, holder.img_pause);

		// Set onClickListener for video
		holder.videoView.setOnClickListener(v -> {
			VideoView videoView = holder.videoView;
			ImageView imgPause = holder.img_pause;

			if (videoView.isPlaying()) {
				Log.i(TAG, "onClick: pause");
				pauseVideo(videoView, imgPause);
			} else {
				Log.i(TAG, "onClick: play");
				playVideo(videoView, imgPause);
			}
		});


		// Set onPreparedListener for video
		holder.videoView.setOnPreparedListener(mp -> {
			mp.setLooping(true);
			// Log
			Log.i(TAG, "Loading video...");
			playVideo(holder.videoView, holder.img_pause);
		});

		// Set onClickListener for img_comment
		holder.img_comment.setOnClickListener(v -> {
			// Add layout_comment to MainActivity
			((MainActivity) context)
					.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_comment_container, new CommentFragment(video, context)).commit();
			((MainActivity) context).findViewById(R.id.fragment_comment_container).setVisibility(View.VISIBLE);
		});

		// Set onClickListener for img_like
		holder.img_like.setOnClickListener(v -> {
			if (MainActivity.getCurrentUser() == null)
				Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
			else {
				// Log
				Log.i(TAG, "Likes list: " + video.getLikes());
				if (video.isLiked())
					VideoFirebase.unlikeVideo(video);
				else
					VideoFirebase.likeVideo(video);
			}
		});
	}

	@Override
	public void onBindViewHolder(@NonNull VideoViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (payloads.isEmpty())
			super.onBindViewHolder(holder, position, payloads);
		else if (position >= videos.size())
			notifyItemInserted(position);
		else {
			if (payloads.get(0) instanceof Video) {
				Video video = (Video) payloads.get(0);
				videos.set(position, video);

				holder.txt_num_likes.setText(String.valueOf(video.getNumLikes()));
				holder.txt_num_comments.setText(String.valueOf(video.getNumComments()));
				if (video.isLiked()) {
					holder.img_like.setImageResource(R.drawable.ic_liked);
				} else {
					holder.img_like.setImageResource(R.drawable.ic_like);
				}

				// Set onClickListener for img_like
				holder.img_like.setOnClickListener(v -> {
					if (MainActivity.getCurrentUser() == null)
						Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
					else {
						// Log
						Log.i(TAG, "Likes list: " + video.getLikes());
						if (video.isLiked())
							VideoFirebase.unlikeVideo(video);
						else
							VideoFirebase.likeVideo(video);
					}
				});
			}
		}
	}

	private void initVideo(VideoView videoView, Video video) {
		videoView.setVideoURI(Uri.parse(video.getLinkVideo()));
		videoView.requestFocus();
		Log.i(TAG, "initVideo: " + video.getVideoId());
	}

	private void initVideoAndDownload(VideoView videoView, Video video) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Handler handler = new Handler(Looper.getMainLooper());
		String path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + video.getVideoId() + ".mp4";
		File file = new File(path);
		AtomicReference<Uri> uri = new AtomicReference<>(Uri.parse(path));
		if (!file.exists()) {
			Log.i(TAG, "Video is not downloaded");
			uri.set(Uri.parse(video.getLinkVideo()));
			executor.execute(() -> {
				// Log
				Log.i(TAG, "Downloading video...");

				// Download video in background
				MyUtil.downloadFile(video.getLinkVideo(), file);

				// Stream video in main thread
				handler.post(() -> {
					// Log
					Log.i(TAG, "Streaming video...");

					// Set video uri
					uri.set(Uri.parse(video.getLinkVideo()));
					videoView.setVideoURI(uri.get());
				});
			});
		} else {
			Log.i(TAG, "Video is downloaded");
		}
		videoView.setVideoURI(uri.get());
		videoView.requestFocus();
		Log.i(TAG, "initVideo: " + video.getVideoId());
	}

	@Override
	public void onViewRecycled(@NonNull VideoViewHolder holder) {
		super.onViewRecycled(holder);
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

	static class VideoViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
		VideoView videoView;
		ImageView img_comment, img_pause, img_like;

		public VideoViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_username);
			txt_content = itemView.findViewById(R.id.txt_content);
			txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
			txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
			videoView = itemView.findViewById(R.id.videoView);
			img_comment = itemView.findViewById(R.id.img_comment);
			img_pause = itemView.findViewById(R.id.img_pause);
			img_like = itemView.findViewById(R.id.img_like);
		}
	}

	@Override
	public long getItemId(int position) {
		Video video = videos.get(position);
		return video.getVideoId().hashCode();
	}
}