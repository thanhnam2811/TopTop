package com.toptop;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.models.Comment;
import com.toptop.models.Video;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.CommentFirebase;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WatchVideoActivity extends FragmentActivity {
	// Tag
	private static final String TAG = "WatchVideoActivity";

	RecyclerView recyclerView;
	Video video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_video);
		getWindow().setNavigationBarColor(Color.WHITE);
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_DARK_MODE, this);

		// Get the Intent that started this activity and extract the string
		Bundle extras = getIntent().getExtras();
		video = (Video) extras.getSerializable(Video.TAG);
		recyclerView = findViewById(R.id.recycler_view_videos);
		Context context = this;

		Query query = FirebaseUtil.getVideoById(video.getVideoId());
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Log.i(TAG, "onDataChange: Video has changed");
				if (snapshot.exists()) {
					video = new Video(snapshot.getChildren().iterator().next());
					if (recyclerView.getAdapter() == null) {
						Log.i(TAG, "onDataChange: " + video.getVideoId());
						List<Video> videos = new ArrayList<>();
						videos.add(video);
						VideoFragmentAdapter adapter = new VideoFragmentAdapter(videos, context);
						recyclerView.setAdapter(adapter);
						recyclerView.setLayoutManager(new LinearLayoutManager(context));
					} else {
						Log.i(TAG, "onDataChange: update");
						VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
						adapter.notifyItemChanged(0, video);
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});

		SnapHelper snapHelper = new PagerSnapHelper();
		snapHelper.attachToRecyclerView(recyclerView);

		ImageView backButton = findViewById(R.id.ic_back);
		backButton.setOnClickListener(v -> finish());

		EditText txt_comment_input = findViewById(R.id.txt_comment_input);
		ImageView ic_send_comment = findViewById(R.id.ic_send_comment);
		txt_comment_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
				String content = charSequence.toString().trim();
				if (content.length() > 0)
					ic_send_comment.setVisibility(View.VISIBLE);
				else ic_send_comment.setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable editable) {
			}
		});

		// Set onClickListener for ic_send_comment
		ic_send_comment.setOnClickListener(v -> handleSendComment(txt_comment_input));
	}

	private void handleSendComment(EditText txt_comment_input) {
		// Get comment content
		String content = txt_comment_input.getText().toString().trim();
		Comment newComment = new Comment();
		newComment.setContent(content);
		newComment.setTime(MyUtil.dateTimeToString(new Date()));
		newComment.setUsername(MainActivity.getCurrentUser().getUsername());
		newComment.setVideoId(video.getVideoId());

		// Add comment to firebase
		CommentFirebase.addCommentToVideo(newComment, video);

		// Toast message
		Toast.makeText(this, "Bình luận thành công!", Toast.LENGTH_SHORT).show();

		// Clear comment input
		txt_comment_input.setText("");

		// Hide keyboard
		KeyboardUtils.hideKeyboard(this);
	}

	public void hideCommentFragment() {
		if (findViewById(R.id.fragment_comment_container) != null && findViewById(R.id.fragment_comment_container).getVisibility() == View.VISIBLE) {
			Log.i(TAG, "onBackPressed: HIDE KEYBOARD");
			KeyboardUtils.hideKeyboard(this);
			Log.i(TAG, "onBackPressed: HIDE LAYOUT_COMMENT");
			findViewById(R.id.fragment_comment_container).setVisibility(View.GONE);

			// Enable scroll
			RecyclerView recycler_view_videos = findViewById(R.id.recycler_view_videos);
			recycler_view_videos.removeOnItemTouchListener(VideoFragmentAdapter.disableTouchListener);
		}
	}
}