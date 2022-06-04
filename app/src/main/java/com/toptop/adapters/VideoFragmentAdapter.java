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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.fragment.CommentFragment;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.RecyclerViewDisabler;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class VideoFragmentAdapter extends RecyclerView.Adapter<VideoFragmentAdapter.VideoViewHolder> {
	private static final String TAG = "VideoFragementAdapter";
	private static final String DEF_AVATAR = "https://firebasestorage.googleapis.com/v0/b/toptop-4d369.appspot.com/o/user-default.png?alt=media&token=6a578948-c61e-4aef-873b-9b2ecc39f15e";
	public static RecyclerView.OnItemTouchListener disableTouchListener = new RecyclerViewDisabler();
	Context context;
	Map<String, Boolean> isVideoInitiated = new HashMap<>();
	private final List<Video> videos;
	private final List<VideoViewHolder> holders = new ArrayList<>();

	public VideoFragmentAdapter(List<Video> videos, Context context) {
		this.videos = videos;
		this.context = context;
	}

	@Override
	public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
		super.onViewAttachedToWindow(holder);
	}

	@NonNull
	@Override
	public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.video_layout, parent, false);

		return new VideoViewHolder(view);
	}

	public void openCommentFragment(Video video, String commentId) {
		handleClickComment(video, commentId);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
		// Check video is exist or not
		if (videos.get(position) == null)
			return;

		holders.add(holder);

		// Set info video once
		Video video = videos.get(position);
		holder.txt_username.setText(video.getUsername());
		holder.txt_content.setText(video.getContent());

		// Load avatar
		UserFirebase.getUserByUsernameOneTime(video.getUsername(),
				user -> {
					try {
						Glide.with(context)
								.load(user.getAvatar())
								.error(R.drawable.default_avatar)
								.into(holder.img_avatar);
					} catch (Exception e) {
						Log.w(TAG, "Glide error: " + e.getMessage());
					}
				}, databaseError -> {
					Log.e(TAG, "updateUI: " + databaseError.getMessage());
					try {
						Glide.with(context)
								.load(R.drawable.default_avatar)
								.into(holder.img_avatar);
					} catch (Exception e) {
						Log.w(TAG, "Glide error: " + e.getMessage());
					}
				}
		);

		// Set onPreparedListener for video
		holder.videoView.setOnPreparedListener(mp -> mp.setLooping(true));

		updateUI(holder, video);

		boolean isLoaded = false;
		if (isVideoInitiated.containsKey(video.getVideoId())) {
			isLoaded = Boolean.TRUE.equals(isVideoInitiated.get(video.getVideoId()));
		} else {
			isVideoInitiated.put(video.getVideoId(), false);
		}

		if (isLoaded)
			playVideo(holder.videoView, holder.img_pause);
		else {
			Log.w(TAG, "onBindViewHolder: Initiate video");
			VideoFirebase.addView(video);
			initVideo(holder.videoView, video);
		}
	}

	private void handleClickAvatar(Video video) {
		MyUtil.goToUser(context, video.getUsername());
	}

	private void updateUI(VideoViewHolder holder, Video video) {
		// Set info video if change
		holder.txt_num_likes.setText(MyUtil.getNumberToText(video.getNumLikes(), 2));
		holder.txt_num_comments.setText(MyUtil.getNumberToText(video.getNumComments(), 2));

		// Set img_follow
		if (!MainActivity.isLoggedIn() || MainActivity.getCurrentUser().getUsername().equals(video.getUsername())) {
			holder.img_follow.setVisibility(View.GONE);
		} else {
			if (MainActivity.getCurrentUser().isFollowing(video.getUsername())) {
				holder.img_follow.setImageResource(R.drawable.ic_following);
			} else {
				holder.img_follow.setImageResource(R.drawable.ic_follow);
			}
		}

		// Check if video is liked or not
		if (!MainActivity.isLoggedIn() || !video.isLiked()) {
			holder.img_like.setImageResource(R.drawable.ic_like);
		} else {
			holder.img_like.setImageResource(R.drawable.ic_liked);
		}

		// Set onClickListener for video
		holder.videoView.setOnClickListener(v ->
				handleClickVideo(holder.videoView, holder.img_pause));

		// Set onClickListener for img_comment
		holder.img_comment.setOnClickListener(v ->
				handleClickComment(video, null));

		// Set onClickListener for img_like
		holder.img_like.setOnClickListener(v ->
				handleClickLike(video));

		// Set onClickListener for img_follow
		holder.img_follow.setOnClickListener(v ->
				handleClickFollow(video));

		// Set onClickListener for img_avatar
		holder.img_avatar.setOnClickListener(v -> handleClickAvatar(video));

		// Set onClickListener for img_share
		holder.img_share.setOnClickListener(v -> handleClickShare(video));
	}

	private void handleClickShare(Video video) {
		// Share video
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, video.getLinkVideo());
		context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.share)));
	}

	private void handleClickFollow(Video video) {
		if (!MainActivity.isLoggedIn())
			Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
		else {
			User user = MainActivity.getCurrentUser();
			if (user.isFollowing(video.getUsername()))
				UserFirebase.unfollowUser(video.getUsername());
			else
				UserFirebase.followUser(video.getUsername());

			notifyItemChanged(videos.indexOf(video), video);
		}
	}

	private void handleClickLike(Video video) {
		if (MainActivity.isLoggedIn()) {
			if (video.isLiked())
				VideoFirebase.unlikeVideo(video);
			else
				VideoFirebase.likeVideo(video);
		} else
			Toast.makeText(context, "Bạn cần đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
	}

	private void handleClickComment(Video video, String commentId) {
		FragmentActivity activity = (FragmentActivity) context;

		CommentFragment commentFragment;

		if (commentId == null)
			commentFragment = CommentFragment.getInstance(video, context);
		else
			commentFragment = CommentFragment.getInstance(video, context, commentId);

		// Add layout_comment to MainActivity
		activity.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_comment_container, commentFragment)
				.commit();

		// Show layout_comment
		activity.findViewById(R.id.fragment_comment_container).setVisibility(View.VISIBLE);
	}

	private void handleClickVideo(VideoView videoView, ImageView img_pause) {
		if (videoView.isPlaying()) {
			Log.i(TAG, "onClick: pause");
			pauseVideo(videoView, img_pause);
		} else {
			Log.i(TAG, "onClick: play");
			playVideo(videoView, img_pause);
		}
	}

	@Override
	public void onBindViewHolder(@NonNull VideoViewHolder holder, int position,
	                             @NonNull List<Object> payloads) {
		if (payloads.isEmpty())
			super.onBindViewHolder(holder, position, payloads);
		else if (position >= videos.size())
			notifyItemInserted(position);
		else {
			if (payloads.get(0) instanceof Video) {
				Video video = (Video) payloads.get(0);
				updateUI(holder, video);
			}
		}
	}

	private void initVideo(VideoView videoView, Video video) {
		if (video.getLinkVideo() != null) {
			videoView.setVideoURI(Uri.parse(video.getLinkVideo()));
			videoView.requestFocus();
			videoView.start();
			Log.i(TAG, "initVideo: " + video.getVideoId());
			isVideoInitiated.put(video.getVideoId(), true);
		} else {
			Toast.makeText(context, "Video không tồn tại", Toast.LENGTH_SHORT).show();
		}
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

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> newVideos) {
		for (Video video : newVideos) {
			if (!videos.contains(video)) {
				videos.add(video);
				notifyItemInserted(videos.size() - 1);
			} else {
				int index = videos.indexOf(video);
				notifyItemChanged(index, video);
			}
		}

		for (Video video : videos) {
			if (!newVideos.contains(video)) {
				int index = videos.indexOf(video);
				videos.remove(video);
				notifyItemRemoved(index);
			}
		}
	}

	@Override
	public long getItemId(int position) {
		Video video = videos.get(position);
		return video.getVideoId().hashCode();
	}

	public void pauseVideo() {
		for (VideoViewHolder holder : holders) {
			pauseVideo(holder.videoView, holder.img_pause);
		}
	}


	static class VideoViewHolder extends RecyclerView.ViewHolder {
		TextView txt_username, txt_content, txt_num_likes, txt_num_comments;
		VideoView videoView;
		ImageView img_comment, img_pause, img_like, img_follow, img_avatar, img_share;

		public VideoViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_email);
			txt_content = itemView.findViewById(R.id.txt_content);
			txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
			txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
			videoView = itemView.findViewById(R.id.videoView);
			img_comment = itemView.findViewById(R.id.img_comment);
			img_pause = itemView.findViewById(R.id.img_pause);
			img_like = itemView.findViewById(R.id.img_like);
			img_follow = itemView.findViewById(R.id.img_follow);
			img_avatar = itemView.findViewById(R.id.img_avatar);
			img_share = itemView.findViewById(R.id.img_share);
		}
	}
}