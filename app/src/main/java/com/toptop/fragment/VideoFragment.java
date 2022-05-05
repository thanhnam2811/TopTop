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
	Context context;
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

		context = view.getContext();

		recyclerView = view.findViewById(R.id.recycler_view_videos);
		recyclerView.setHasFixedSize(true);

		getVideosFromFirebase();

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

	private void getVideosFromFirebase() {
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
					VideoFragementAdapter videoFragementAdapter = new VideoFragementAdapter(videos, context);
					videoFragementAdapter.setHasStableIds(true);
					recyclerView.setAdapter(videoFragementAdapter);
				} else {
					recyclerView.getAdapter().notifyItemRangeChanged(0, videos.size());
				}
			}
		});

		// For everytime, update videos if there is any change
		mDatabase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				int position = 0;
				VideoFragementAdapter videoFragementAdapter = (VideoFragementAdapter) recyclerView.getAdapter();
				if (videoFragementAdapter != null) {
					for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
						Video video = new Video(dataSnapshot);
						if (videos.size() > position) {
							if (!videos.get(position).equals(video)) {
								videos.set(position, video);
								videoFragementAdapter.notifyItemChanged(position, video);
							} else {
								videos.add(video);
								videoFragementAdapter.notifyItemChanged(position, video);
							}
							position++;
						}
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Log.e(TAG, "onCancelled: ", error.toException());
			}
		});

	}
}