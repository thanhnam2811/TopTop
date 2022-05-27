package com.toptop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.NotificationFragmentAdapter;
import com.toptop.models.Notification;
import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.NotificationFirebase;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
	public static final String TAG = "NotificationFragment";
	private final ArrayList<Notification> notifications = new ArrayList<>();

	RecyclerView recyclerView;
	Context context;

	private static final NotificationFragment instance = new NotificationFragment();

	private NotificationFragment() {

	}

	public static NotificationFragment getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notification, container, false);
		context = view.getContext();

		recyclerView = view.findViewById(R.id.recycler_view_notifications);

		updateUI();

		// Set status bar color
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, requireActivity());

		// Inflate the layout for this fragment
		return view;
	}

	public void updateUI() {
		if (MainActivity.isLoggedIn()) {
			User usercurrent = MainActivity.getCurrentUser();
			if (usercurrent != null) {
				updateUI(usercurrent);
			} else {
				Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void updateUI(User user) {
		NotificationFirebase.getNotificationByUsername(user.getUsername(),
				listNotifications -> {
					notifications.clear();
					notifications.addAll(listNotifications);
					// Set seen TRUE notifications
					Notification.setSeen(notifications);
					NotificationFragmentAdapter notificationFragmentAdapter = new NotificationFragmentAdapter(notifications, context);
					if (recyclerView.getAdapter() == null) {
						recyclerView.setAdapter(notificationFragmentAdapter);

						LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
						linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
						recyclerView.setLayoutManager(linearLayoutManager);
					}
				}, error -> {
					Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
				});
	}
}