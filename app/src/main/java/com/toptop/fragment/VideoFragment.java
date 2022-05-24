package com.toptop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.toptop.R;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
	public static final String TAG = "VideoFragment";
	DatabaseReference mDatabase;
	Context context;
	RecyclerView recyclerView;
	private List<Video> videos = new ArrayList<>();

	private static final VideoFragment instance = new VideoFragment();

	private VideoFragment() { }

	public static VideoFragment getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Log
		Log.d(TAG, "onCreateView: ");

		View view = inflater.inflate(R.layout.fragment_video, container, false);

		context = view.getContext();

		recyclerView = view.findViewById(R.id.recycler_view_videos);
		recyclerView.setHasFixedSize(true);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		recyclerView.setLayoutManager(linearLayoutManager);

		loadVideos();

		SnapHelper snapHelper = new PagerSnapHelper();
		snapHelper.attachToRecyclerView(recyclerView);

		// Set status bar color
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_DARK_MODE, requireActivity());

		// Inflate the layout for this fragment
		return view;
	}

	private void loadVideos() {
		if (recyclerView.getAdapter() == null) {
			mDatabase = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);

			// For first time, get all videos
			mDatabase.get().addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					videos.clear();
					DataSnapshot dataSnapshot = task.getResult();
					for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
						Video video = new Video(snapshot);
						videos.add(video);
					}
					if (recyclerView.getAdapter() == null) {
						VideoFragmentAdapter videoFragmentAdapter = new VideoFragmentAdapter(videos, context);
						videoFragmentAdapter.setHasStableIds(true);
						recyclerView.setAdapter(videoFragmentAdapter);
//					recyclerView.smoothScrollToPosition(2);
					} else {
						recyclerView.getAdapter().notifyItemRangeChanged(0, videos.size());
					}
				}
			});

			// For everytime, update videos if there is any change
			mDatabase.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					VideoFragmentAdapter videoFragmentAdapter = (VideoFragmentAdapter) recyclerView.getAdapter();
					if (videoFragmentAdapter != null) {
						videos = videoFragmentAdapter.getVideos();
						for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
							Video video = new Video(dataSnapshot);
							if (videos.contains(video)) {
								videos.set(videos.indexOf(video), video);
							} else {
								videos.add(video);
							}
						}
						videoFragmentAdapter.setVideos(videos);
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					Log.e(TAG, "onCancelled: ", error.toException());
				}
			});
		}
	}

	public void goToVideo(Video video) {
		recyclerView.smoothScrollToPosition(videos.indexOf(video));
	}

	public void updateUI() {
		if (recyclerView.getAdapter() != null) {
			videos = ((VideoFragmentAdapter) recyclerView.getAdapter()).getVideos();
			recyclerView.getAdapter().notifyItemRangeChanged(0, videos.size());
		}
	}

	public void updateUI(User user) {
		if (recyclerView.getAdapter() != null) {
			videos = ((VideoFragmentAdapter) recyclerView.getAdapter()).getVideos();
			recyclerView.getAdapter().notifyItemRangeChanged(0, videos.size());
		}
	}
}