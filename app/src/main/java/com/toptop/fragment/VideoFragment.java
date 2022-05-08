package com.toptop.fragment;

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
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.VideoFragementAdapter;
import com.toptop.models.Video;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
	private static final String TAG = "VideoFragment";
	private ArrayList<Video> videos = new ArrayList<>();
	DatabaseReference mDatabase;
	RecyclerView recyclerView;

	public VideoFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_video, container, false);

		 recyclerView = view.findViewById(R.id.recycler_view_videos);

		mDatabase = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_VIDEOS);
		mDatabase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				videos.clear();
				for (DataSnapshot dataSnapshot : snapshot.getChildren())
					videos.add(new Video(dataSnapshot));

				VideoFragementAdapter videoFragementAdapter = new VideoFragementAdapter(videos, view.getContext());
				if (recyclerView.getAdapter() == null)
					recyclerView.setAdapter(videoFragementAdapter);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "onCancelled: ", error.toException());
			}
		});

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		recyclerView.setLayoutManager(linearLayoutManager);

		SnapHelper snapHelper = new PagerSnapHelper();
		snapHelper.attachToRecyclerView(recyclerView);

		recyclerView.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}
		});

		// Set status bar color
		((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_DARK_MODE);

		// Inflate the layout for this fragment
		return view;
	}

	public void goToVideo(Video video) {
		for (int i = 0; i < videos.size(); i++) {
			if (videos.get(i).getVideoId().equals(video.getVideoId())) {
				// Log
				Log.d(TAG, "goToVideo: " + i);
				recyclerView.smoothScrollToPosition(i);
				break;
			}
		}
	}
}