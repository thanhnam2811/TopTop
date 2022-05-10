package com.toptop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.NotificationFragmentAdapter;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
	public static final String TAG = "NotificationFragment";
	DatabaseReference mDatabase;
	private final ArrayList<Notification> notifications = new ArrayList<>();

	public NotificationFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notification, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notifications);

		if (MainActivity.isLoggedIn()) {
			User usercurrent = MainActivity.getCurrentUser();
			if (usercurrent != null) {
				Query query = FirebaseUtil.getNotificationsByUsername(usercurrent.getUsername());
				query.addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
						notifications.clear();
						for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
							notifications.add(new Notification(snapshot));
						}
						System.out.println("notifications: " + notifications.size());
						NotificationFragmentAdapter notificationFragmentAdapter = new NotificationFragmentAdapter(notifications, view.getContext());
						if (recyclerView.getAdapter() == null) {
							recyclerView.setAdapter(notificationFragmentAdapter);

							LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
							linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
							recyclerView.setLayoutManager(linearLayoutManager);
						}
					}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {
						System.out.println("error: " + error.getMessage());
					}
				});
			} else {
				Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
			}
		}

		// Set status bar color
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, requireActivity());

		// Inflate the layout for this fragment
		return view;
	}
}