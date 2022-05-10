package com.toptop;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
import com.toptop.models.Video;
import com.toptop.utils.KeyboardUtils;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class WatchVideoActivity extends FragmentActivity {
	// Tag
	private static final String TAG = "WatchVideoActivity";

	RecyclerView recyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_video);

		// Get the Intent that started this activity and extract the string
		Bundle extras = getIntent().getExtras();
		Video video = (Video) extras.getSerializable(Video.TAG);
		recyclerView = findViewById(R.id.recycler_view_videos);
		Context context = this;

		Query query = FirebaseUtil.getVideoById(video.getVideoId());
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Video newVideo = new Video(snapshot);
				if (video.hasChanged(newVideo) || recyclerView.getAdapter() == null) {
					List<Video> videos = new ArrayList<>();
					videos.add(video);

					if (recyclerView.getAdapter() == null) {
						VideoFragmentAdapter adapter = new VideoFragmentAdapter(videos, context);
						recyclerView.setAdapter(adapter);
						recyclerView.setLayoutManager(new LinearLayoutManager(context));
					} else {
						VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
						adapter.setVideos(videos);
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

		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_DARK_MODE, this);
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