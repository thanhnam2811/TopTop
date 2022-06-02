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
import com.toptop.adapters.CommentsManagementAdapter;
import com.toptop.utils.firebase.CommentFirebase;

public class CommentsManagementFragment extends Fragment {
	// Tag
	private static final String TAG = "CommentsManagementFragment";

	public CommentsManagementFragment() {
		// Required empty public constructor
	}

	private static final CommentsManagementFragment instance = new CommentsManagementFragment();

	public static CommentsManagementFragment getInstance() {
		return instance;
	}

	RecyclerView recyclerView;
	Spinner spinnerNumberOfLikes;
	SearchView searchView;
	String filterNumberOfLikes, searchText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_comments_management, container, false);

		// Bind views
		recyclerView = view.findViewById(R.id.recyclerView);
		spinnerNumberOfLikes = view.findViewById(R.id.spinner_num_likes);
		searchView = view.findViewById(R.id.search_comments);

		// Set up spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
				R.array.number_filter, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNumberOfLikes.setAdapter(adapter);

		// Set up recycler view
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
				DividerItemDecoration.VERTICAL);
		recyclerView.addItemDecoration(dividerItemDecoration);

		CommentFirebase.getAllComments(
				comments -> {
					CommentsManagementAdapter commentsManagementAdapter = new CommentsManagementAdapter(comments, getContext());
					recyclerView.setAdapter(commentsManagementAdapter);
				}, error -> {
					Log.e(TAG, "Error getting comments: " + error.getMessage());
				}
		);

		// Set up search view
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

		// Sinner listener
		spinnerNumberOfLikes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterNumberOfLikes = parent.getItemAtPosition(position).toString();
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		});

		return view;
	}

	private void filter() {
		CommentsManagementAdapter commentsManagementAdapter = (CommentsManagementAdapter) recyclerView.getAdapter();
		if (commentsManagementAdapter != null) {
			commentsManagementAdapter.filter(filterNumberOfLikes, searchText);
		}
	}
}