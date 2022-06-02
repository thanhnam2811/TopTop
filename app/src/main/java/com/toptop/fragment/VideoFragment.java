package com.toptop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.database.DatabaseReference;
import com.toptop.R;
import com.toptop.adapters.VideoFragmentAdapter;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends Fragment {
	public static final String TAG = "VideoFragment";
	DatabaseReference mDatabase;
	Context context;
	RecyclerView recyclerView;
	private List<Video> videos = new ArrayList<>();

	private static final VideoFragment instance = new VideoFragment();

	public VideoFragment() { }

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

		// Inflate the layout for this fragment
		return view;
	}

	private void loadVideos() {
		VideoFirebase.getAllVideos(
				listVideos -> {
					VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
					if (adapter != null) {
						adapter.setVideos(listVideos);
					} else {
						adapter = new VideoFragmentAdapter(listVideos, context);
						recyclerView.setAdapter(adapter);
					}
				}, error -> {
					Log.e(TAG, "loadVideos: " + error.getMessage());
				}
		);
	}

	public void pauseVideo() {
		VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
		if (adapter != null) {
			adapter.pauseVideo();
		}
	}

	public void updateUI() {
		if (recyclerView.getAdapter() != null) {
			VideoFragmentAdapter adapter = (VideoFragmentAdapter) recyclerView.getAdapter();
			adapter.setVideos(adapter.getVideos());
		}
	}

	public void updateUI(User user) {
		if (recyclerView.getAdapter() != null) {
			videos = ((VideoFragmentAdapter) recyclerView.getAdapter()).getVideos();
			recyclerView.getAdapter().notifyItemRangeChanged(0, videos.size());
		}
	}
}