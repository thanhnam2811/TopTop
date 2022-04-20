package com.toptop.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.toptop.R;
import com.toptop.adapters.NotificationAdapter;
import com.toptop.models.Notification;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        //        1. create notification data
        ArrayList<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(1, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
        notifications.add(new Notification(2, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước", R.drawable.avatar));
        notifications.add(new Notification(3, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
        notifications.add(new Notification(4, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
        notifications.add(new Notification(5, R.drawable.demo_avatar, "Thái Thành Nam", "Đẹp gái quá.. Làm ny mình nha","10p trước",R.drawable.avatar));
        // 2 binding listview
        ListView listView = view.findViewById(R.id.listNotification);
        //  3. create adapter
        NotificationAdapter adapter = new NotificationAdapter(this, R.layout.item_listinform);
        // 4. set adapter for listview
        adapter.addAll(notifications);
        listView.setAdapter(adapter);

        // Set status bar color
        requireActivity().getWindow().setStatusBarColor(Color.WHITE);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Inflate the layout for this fragment
        return view;
    }
}