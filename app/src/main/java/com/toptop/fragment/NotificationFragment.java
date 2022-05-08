package com.toptop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.adapters.NotificationAdapter;
import com.toptop.models.Notification;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

	public static final String TAG = "NotificationFragment";

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

        //        1. create notification data
        ArrayList<Notification> notifications = new ArrayList<>();
//        notifications.add(new Notification(1, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
//        notifications.add(new Notification(2, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước", R.drawable.avatar));
//        notifications.add(new Notification(3, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
//        notifications.add(new Notification(4, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
//        notifications.add(new Notification(5, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
        // 2 binding listview
        ListView listView = view.findViewById(R.id.listNotification);
        //  3. create adapter
        NotificationAdapter adapter = new NotificationAdapter(this, R.layout.item_listinform);
        // 4. set adapter for listview
        adapter.addAll(notifications);
        listView.setAdapter(adapter);

        // Set status bar color
        ((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_LIGHT_MODE);

        // Inflate the layout for this fragment
        return view;
    }
}