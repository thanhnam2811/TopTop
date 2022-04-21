package com.toptop.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.toptop.LoginActivity;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.models.Comment;
import com.toptop.models.Video;
import com.toptop.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class VideoFragementAdapter extends RecyclerView.Adapter<VideoFragementAdapter.VideoViewHolder> {
	private static final String TAG = "VideoFragementAdapter";

	private DatabaseReference mDB_comment;

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
		mDB_comment = new FirebaseUtil("comments").getDatabase();
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
						String formattedTime = (String) map.get("time");
						Comment comment = new Comment(comment_id, username, video_id, content, formattedTime);
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
				Log.e(TAG, "failed to read value from firebase", error.toException());
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
			Log.i(TAG, "onClick video");
			VideoView videoView = holder.videoView;
			ImageView imgPause = holder.img_pause;
			if (videoView.isPlaying())
				pauseVideo(videoView, imgPause);
			else
				playVideo(videoView, imgPause);
			if (holder.layout_comment.getVisibility() == View.VISIBLE)
				((MainActivity) context).onBackPressed();
		});

		// Set onClickListener for img_comment
		holder.img_comment.setOnClickListener(v -> {
			// Add layout_comment to MainActivity
			((MainActivity) context)
					.addContentView(holder.layout_comment,
							new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			EditText txt_input_comment = holder.layout_comment.findViewById(R.id.input_comment).findViewById(R.id.txt_comment_input);
			ImageView ic_send_comment = holder.layout_comment.findViewById(R.id.input_comment).findViewById(R.id.ic_send_comment);
			txt_input_comment.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
					String content = charSequence.toString().trim();
					System.out.println(content);
					if (content.length() > 0)
						ic_send_comment.setVisibility(View.VISIBLE);
					else ic_send_comment.setVisibility(View.GONE);
				}

				@Override
				public void afterTextChanged(Editable editable) {
				}
			});

			ic_send_comment.setOnClickListener(view -> {
				mDB_comment = new FirebaseUtil("comments").getDatabase();
				String content = txt_input_comment.getText().toString().trim();
				Comment newComment = new Comment(mDB_comment.getKey(), "thanhnam1324", "hsdhgsdgh", content, new Date());
				mDB_comment.push().setValue(newComment);
				Toast.makeText(context, "Comment success", Toast.LENGTH_SHORT).show();
			});

			holder.layout_comment.setVisibility(View.VISIBLE);
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
		linkVideo = "https://vimeo.com/701098493";
		try {
			videoView.setVideoURI(Uri.parse(linkVideo));
			videoView.requestFocus();
			System.out.println("Init Video");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void playVideo(VideoView videoView, ImageView imgPause) {
		videoView.start();
		imgPause.setVisibility(View.GONE);
	}

	public void pauseVideo(VideoView videoView, ImageView imgPause) {
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
		ImageView img_comment, img_pause, ic_send_comment;
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
			ic_send_comment = itemView.findViewById(R.id.ic_send_comment);
		}
	}
}
