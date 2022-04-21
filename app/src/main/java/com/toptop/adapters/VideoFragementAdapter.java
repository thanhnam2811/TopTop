package com.toptop.adapters;

import static com.toptop.utils.MyUtil.getDateFromFirebase;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptop.LoginActivity;
import com.toptop.R;
import com.toptop.models.Comment;
import com.toptop.models.Video;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VideoFragementAdapter extends RecyclerView.Adapter<VideoFragementAdapter.VideoViewHolder> {
	private static final String LOG_TAG = "AndroidVideoView";

	private DatabaseReference mDB_comment;
	private final String FIREBASE_URL = "https://toptop-4d369-default-rtdb.asia-southeast1.firebasedatabase.app/";

	private final List<Video> videos;
	Context context;

	@Override
	public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
		super.onViewAttachedToWindow(holder);
		playVideo(holder.videoView, holder.img_pause);
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

	@Override
	public void onBindViewHolder(VideoViewHolder holder, int position) {
		holder.recycler_comment.removeAllViews();

		// Get comments from firebase
		mDB_comment = FirebaseDatabase.getInstance(FIREBASE_URL).getReference("comments");
		mDB_comment.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				List<Comment> comments = new ArrayList<>();
				for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
					try {
						Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
						String comment_id = (String) map.get("comment_id");
						String username = (String) map.get("username");
						String video_id = (String) map.get("video_id");
						String content = (String) map.get("content");
						Date time = getDateFromFirebase( (String) map.get("time"));
						Comment comment = new Comment(comment_id, username, video_id, content, time);
						comments.add(comment);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				comments.addAll(comments);
				CommentFragmentAdapter adapter = new CommentFragmentAdapter(comments, context);
				holder.recycler_comment.setAdapter(adapter);
				LinearLayoutManager layoutManager = new LinearLayoutManager(context);
				layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
				holder.recycler_comment.setLayoutManager(layoutManager);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				System.out.println(error.getMessage());
			}
		});

		// Set info video
		Video video = videos.get(position);
		holder.txt_username.setText(video.getUsername());
		holder.txt_content.setText(video.getContent());
		holder.txt_num_likes.setText(String.valueOf(video.getTotalLikes()));
		holder.txt_num_comments.setText(String.valueOf(video.getTotalComments()));

		// Turn off scrollable when layout comment is visible
		holder.layout_comment.setOnTouchListener((view, motionEvent) -> {
			holder.layout_comment.getParent().requestDisallowInterceptTouchEvent(true);
			return false;
		});
		holder.recycler_comment.setOnTouchListener((view, motionEvent) -> {
			holder.recycler_comment.getParent().requestDisallowInterceptTouchEvent(true);
			return false;
		});

		playVideo(holder.videoView, holder.img_pause);

		// Set onClickListener for video
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

		// Set onClickListener for img_comment
		holder.img_comment.setOnClickListener(v -> {
			holder.layout_comment.setVisibility(View.VISIBLE);

		});
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
		VideoView videoView;
		ImageView img_comment, img_pause;
		LinearLayout layout_comment;
		RecyclerView recycler_comment;

		public VideoViewHolder(@NonNull View itemView) {
			super(itemView);
			txt_username = itemView.findViewById(R.id.txt_username);
			txt_content = itemView.findViewById(R.id.txt_content);
			txt_num_likes = itemView.findViewById(R.id.txt_num_likes);
			txt_num_comments = itemView.findViewById(R.id.txt_num_comments);
			videoView = itemView.findViewById(R.id.videoView);
			img_comment = itemView.findViewById(R.id.img_comment);
			img_pause = itemView.findViewById(R.id.img_pause);
			layout_comment = itemView.findViewById(R.id.layout_comment);
			recycler_comment = itemView.findViewById(R.id.recycler_view_comments);
		}
	}
}
