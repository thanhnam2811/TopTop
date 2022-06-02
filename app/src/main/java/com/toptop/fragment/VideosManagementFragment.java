package com.toptop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.R;
import com.toptop.adapters.VideosManagementAdapter;
import com.toptop.models.User;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;
import java.util.List;

public class VideosManagementFragment extends Fragment {
	// Tag
	private static final String TAG = "VideosManagementFragment";

	public VideosManagementFragment() {
		// Required empty public constructor
	}

	private static final VideosManagementFragment instance = new VideosManagementFragment();

	public static VideosManagementFragment getInstance() {
		return instance;
	}

	RecyclerView recyclerView;
	Spinner spinnerNumberOfViews, spinnerNumberOfLikes, spinnerNumberOfComments;
	SearchView searchView;
	String filterNumberOfViews, filterNumberOfLikes, filterNumberOfComments, searchText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_videos_management, container, false);

		// Bind views
		recyclerView = view.findViewById(R.id.recyclerView);
		spinnerNumberOfViews = view.findViewById(R.id.spinner_num_views);
		spinnerNumberOfLikes = view.findViewById(R.id.spinner_num_likes);
		spinnerNumberOfComments = view.findViewById(R.id.spinner_num_comments);
		searchView = view.findViewById(R.id.search_videos);

		// Set up spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
				R.array.number_filter, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNumberOfViews.setAdapter(adapter);
		spinnerNumberOfLikes.setAdapter(adapter);
		spinnerNumberOfComments.setAdapter(adapter);

		// Set recycler view
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(dividerItemDecoration);

		VideoFirebase.getAllVideos(
				videos -> {
					VideosManagementAdapter videosManagementAdapter = new VideosManagementAdapter(videos, getContext());
					recyclerView.setAdapter(videosManagementAdapter);
				}, error -> {
					Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
					Log.e(TAG, "onCreateView: ", error.toException());
				}
		);

		// Set up spinner listeners
		spinnerNumberOfViews.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterNumberOfViews = parent.getItemAtPosition(position).toString();
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		}
		);

		spinnerNumberOfLikes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterNumberOfLikes = parent.getItemAtPosition(position).toString();
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		}
		);

		spinnerNumberOfComments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterNumberOfComments = parent.getItemAtPosition(position).toString();
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		}
		);

		// Set up searchView listener
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchText = query;
				filter();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				searchText = newText;
				filter();
				return false;
			}
		});

		return view;
	}

	private void filter() {
		VideosManagementAdapter videosManagementAdapter = (VideosManagementAdapter) recyclerView.getAdapter();
		if (videosManagementAdapter != null) {
			videosManagementAdapter.filter(filterNumberOfViews, filterNumberOfLikes, filterNumberOfComments, searchText);
		}
	}
}