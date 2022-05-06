package com.toptop.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.R;
//import com.toptop.adapters.ListSearchAdapter;
import com.toptop.adapters.NotificationFragmentAdapter;
import com.toptop.adapters.SearchFragmentAdapter;
import com.toptop.adapters.SearchFragmentVideoAdapter;
import com.toptop.adapters.SearchNotFoundAdapter;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.models.Video;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener{
	private static final String TAG = "SearchFragment";
	private ArrayList<User> users = new ArrayList<>();
	private ArrayList<Video> videos = new ArrayList<>();
	DatabaseReference mDatabase;

	public SearchFragment() {
		// Required empty public constructor
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
		TextView txtSearch = view.findViewById(R.id.txtSearch);
		txtSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchView.setIconified(false);
				String input = searchView.getQuery().toString();
				System.out.println(input);
				if(input.length() <= 0) {
					searchView.setQuery("", false);
				}else {
					searchView.setQuery(input, true);
				}
			}
		});
//		searchView.setOnQueryTextListener(this);
		searchView.setQueryHint("Search");
		searchView.setFocusable(true);
		searchView.requestFocusFromTouch();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					Log.d(TAG, "onQueryTextSubmit: " + query);
//					reset recyclerView
					recyclerView.setAdapter(null);
					recyclerViewForVideo.setAdapter(null);
					//search for user
					Query queryUser = FirebaseUtil.getUserByStringLikeUsername(query);
					queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
							users.clear();
							for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
								users.add(new User(snapshot));
							}
							System.out.println("users: " + users);
							SearchFragmentAdapter searchFragmentAdapter = new SearchFragmentAdapter(users,view.getContext());
							if(recyclerView.getAdapter() == null) {
								recyclerView.setAdapter(searchFragmentAdapter);
							}
							//	search for video
							Query queryVideo = FirebaseUtil.getVideoByStringLikeContent(query);
							queryVideo.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
									videos.clear();
									for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
										videos.add(new Video(snapshot));
									}
									System.out.println("videos: " + videos);
									SearchFragmentVideoAdapter searchFragmentAdapterForVideo = new SearchFragmentVideoAdapter(videos,view.getContext());
									if(recyclerViewForVideo.getAdapter() == null) {
										recyclerViewForVideo.setAdapter(searchFragmentAdapterForVideo);
									}
									if(videos.size() == 0 && users.size() == 0) {
										Toast.makeText(view.getContext(), "No result", Toast.LENGTH_SHORT).show();
										SearchNotFoundAdapter searchNotFoundAdapter = new SearchNotFoundAdapter(view.getContext(),  query + " không cho ra kết quả tìm kiếm");
										recyclerView.setAdapter(searchNotFoundAdapter);
									}

								}

								@Override
								public void onCancelled(@NonNull DatabaseError error) {
									Log.d(TAG, "onCancelled: " + error.getMessage());
								}
							});

						}

						@Override
						public void onCancelled(@NonNull DatabaseError error) {
							Log.d(TAG, "onCancelled: " + error.getMessage());
						}
					});

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

//		recyclerView.setNestedScrollingEnabled(false);
//		recyclerViewForVideo.setNestedScrollingEnabled(false);
//		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//				super.onScrollStateChanged(recyclerView, newState);
//			}
//		});

		// Set status bar color
		((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_DARK_MODE);

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