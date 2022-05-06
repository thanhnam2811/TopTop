package com.toptop.fragment;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.toptop.adapters.NotificationFragmentAdapter;
import com.toptop.adapters.VideoFragementAdapter;
import com.toptop.models.Notification;
import com.toptop.models.*;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.NotificationFirebase;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import java.util.ArrayList;


public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";
    private ArrayList<Notification> notifications = new ArrayList<>();
    DatabaseReference mDatabase;

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

//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notifications);

        mDatabase = FirebaseUtil.getDatabase(FirebaseUtil.TABLE_NOTIFICATIONS);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notifications);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    notifications.add(new Notification(dataSnapshot));
                }
                System.out.println("notifications: " + notifications.size());
                NotificationFragmentAdapter notificationFragmentAdapter = new NotificationFragmentAdapter(notifications, view.getContext());
                if (recyclerView.getAdapter() == null)
                    recyclerView.setAdapter(notificationFragmentAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                recyclerView.setLayoutManager(linearLayoutManager);

                SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);

                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: ", error.toException());
            }
        });

//        set onClickListener for each item in recyclerView

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

        // Set status bar color
        ((MainActivity) requireActivity()).setStatusBarColor(MainActivity.STATUS_BAR_DARK_MODE);

        // Inflate the layout for this fragment
        return view;
    }
}