package com.toptop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.toptop.R;
import com.toptop.adapters.SearchFragmentAdapter;
import com.toptop.adapters.SearchFragmentVideoAdapter;
import com.toptop.adapters.SearchNotFoundAdapter;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;
import com.toptop.utils.firebase.VideoFirebase;

import java.util.ArrayList;

//import com.toptop.adapters.ListSearchAdapter;


public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener {
	public static final String TAG = "SearchFragment";
	DatabaseReference mDatabase;
	private final ArrayList<User> users = new ArrayList<>();
	private final ArrayList<Video> videos = new ArrayList<>();

	private static final SearchFragment instance = new SearchFragment();

	private SearchFragment() {
	}

	public static SearchFragment getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerSearchView);
		RecyclerView recyclerViewForVideo = view.findViewById(R.id.recyclerSearchViewforVideo);
		SearchView searchView = view.findViewById(R.id.searchView);
		ScrollView scrollView = view.findViewById(R.id.scrollViewSearch);
		TextView txtSearch = view.findViewById(R.id.txtSearch);
		TextView lblAccount = view.findViewById(R.id.labelAccounts);
		TextView lblVideo = view.findViewById(R.id.labelVideos);
		//set animation for scrollview
//		scrollView.setAnimation();
		//handle click Search
		txtSearch.setOnClickListener(v -> {
			searchView.setIconified(false);
			String input = searchView.getQuery().toString();
			System.out.println(input);
			if (input.length() <= 0) {
				searchView.setQuery("", false);
				Toast.makeText(getContext(), "Please enter a keyword", Toast.LENGTH_SHORT).show();
			} else {
				searchView.setQuery(input, true);
			}
		});
		//forcus on searchview

		searchView.setQueryHint("Search");
		searchView.setFocusable(true);
		searchView.setIconifiedByDefault(false);
		searchView.requestFocusFromTouch();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "onQueryTextSubmit: " + query);
//				reset recyclerView
				recyclerView.setAdapter(null);
				recyclerViewForVideo.setAdapter(null);
				//reset label
				lblAccount.setVisibility(View.GONE);
				lblVideo.setVisibility(View.GONE);
				//search for user
				UserFirebase.getListUserLikeUsername(listUsers -> {
					users.clear();
					for (User user : listUsers) {
						users.add(user);
					}
					SearchFragmentAdapter searchFragmentAdapter = new SearchFragmentAdapter(users, view.getContext());
					if (recyclerView.getAdapter() == null && users.size() > 0) {
						recyclerView.setAdapter(searchFragmentAdapter);
						lblAccount.setVisibility(View.VISIBLE);
					}
					// Search for video
					VideoFirebase.getVideoLikeContent(query,
							listvideos -> {
						System.out.println(listvideos.size());
								videos.clear();
								videos.addAll(listvideos);
								SearchFragmentVideoAdapter searchFragmentAdapterForVideo = new SearchFragmentVideoAdapter(videos, view.getContext());
								if (recyclerViewForVideo.getAdapter() == null && videos.size() > 0) {
									lblVideo.setVisibility(View.VISIBLE);
									recyclerViewForVideo.setAdapter(searchFragmentAdapterForVideo);
								}
								if (videos.size() == 0 && users.size() == 0) {
									Toast.makeText(view.getContext(), "No result", Toast.LENGTH_SHORT).show();
									SearchNotFoundAdapter searchNotFoundAdapter = new SearchNotFoundAdapter(view.getContext(), query + " không cho ra kết quả tìm kiếm");
									recyclerView.setAdapter(searchNotFoundAdapter);
								}
							}, error -> {
								Toast.makeText(view.getContext(), "No result", Toast.LENGTH_SHORT).show();
							});

				}, query);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.d(TAG, "onQueryTextChange: " + newText);
				return true;
			}
		});


		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
		linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

		recyclerView.setLayoutManager(linearLayoutManager);
		recyclerViewForVideo.setLayoutManager(new GridLayoutManager(view.getContext(), 2));

		recyclerView.setNestedScrollingEnabled(false);
//		recyclerViewForVideo.setNestedScrollingEnabled(false);

		// Set status bar color
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, requireActivity());

		// Inflate the layout for this fragment
		return view;

	}


	@Override
	public boolean onQueryTextSubmit(String query) {
		System.out.println("onQueryTextSubmit: " + query);
		Log.d(TAG, "onQueryTextSubmit: " + query);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String s) {
		System.out.println("onQueryTextChange: " + s);
		Log.d(TAG, "onQueryTextChange: " + s);
		return true;
	}
}