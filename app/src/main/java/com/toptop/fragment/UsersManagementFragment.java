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
import com.toptop.adapters.UsersManagementAdapter;
import com.toptop.models.User;
import com.toptop.utils.firebase.UserFirebase;

public class UsersManagementFragment extends Fragment {
	// tag
	private static final String TAG = "UsersManagementFragment";

	private UsersManagementFragment() {
		// Required empty public constructor
	}

	private static final UsersManagementFragment instance = new UsersManagementFragment();

	public static UsersManagementFragment getInstance() {
		return instance;
	}

	RecyclerView recyclerView;
	Spinner spinnerNumberOfFollowers, spinnerNumberOfFollowing, spinnerNumberOfLikes, spinnerRole;
	SearchView searchView;
	String filterNumberOfFollowers, filterNumberOfFollowing, filterNumberOfLikes, searchText, role = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_users_management, container, false);

		// Bind views
		recyclerView = view.findViewById(R.id.recyclerView);
		spinnerNumberOfFollowers = view.findViewById(R.id.spinner_num_followers);
		spinnerNumberOfFollowing = view.findViewById(R.id.spinner_num_following);
		spinnerNumberOfLikes = view.findViewById(R.id.spinner_num_likes);
		spinnerRole = view.findViewById(R.id.spinner_role);
		searchView = view.findViewById(R.id.search_user);

		// Set adapter
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
				R.array.number_filter, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNumberOfFollowers.setAdapter(adapter);
		spinnerNumberOfFollowing.setAdapter(adapter);
		spinnerNumberOfLikes.setAdapter(adapter);

		ArrayAdapter<CharSequence> adapterRole = ArrayAdapter.createFromResource(getContext(),
				R.array.role_filter, android.R.layout.simple_spinner_item);
		adapterRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRole.setAdapter(adapterRole);

		// Set recycler view
		recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

		UserFirebase.getAllUsers(
				listUsers -> {
					UsersManagementAdapter usersManagementAdapter = new UsersManagementAdapter(listUsers, getContext());
					recyclerView.setAdapter(usersManagementAdapter);
				}, error -> {
					Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
					Log.e(TAG, "onCreateView: ", error.toException());
				}
		);

		// Set listeners
		spinnerNumberOfFollowers.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterNumberOfFollowers = parent.getItemAtPosition(position).toString();
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		});

		spinnerNumberOfFollowing.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				filterNumberOfFollowing = parent.getItemAtPosition(position).toString();
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		});

		spinnerNumberOfLikes.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
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

		spinnerRole.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						role = null;
						break;
					case 1:
						role = User.ROLE_ADMIN;
						break;
					case 2:
						role = User.ROLE_USER;
						break;
				}
				filter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Toast.makeText(getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
			}
		});

		// Set search view
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
		UsersManagementAdapter usersManagementAdapter = (UsersManagementAdapter) recyclerView.getAdapter();
		if (usersManagementAdapter != null) {
			usersManagementAdapter.setFilter(filterNumberOfFollowers, filterNumberOfFollowing, filterNumberOfLikes, searchText, role);
		}
	}
}