package com.toptop.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.toptop.R;
import com.toptop.adapters.VideoAdapter;
import com.toptop.models.Video;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class VideoFragment extends Fragment {
    private ArrayList<Video> videos;

    public VideoFragment() {
        // Required empty public constructor
    }

    private void initData() {
        videos.add(new Video("V001", "thanhnam", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur maximus urna nec sem rhoncus, scelerisque sodales felis tincidunt."
                , "video3135074881", 99999, 123, 99999, new Date()));
        videos.add(new Video("V002", "ngoctrung", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur maximus urna nec sem rhoncus, scelerisque sodales felis tincidunt."
                , "video3135074881", 123321, 123, 99999, new Date()));
        videos.add(new Video("V003", "hoaitan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur maximus urna nec sem rhoncus, scelerisque sodales felis tincidunt."
                , "video3135074881", 231, 21344, 99999, new Date()));
        videos.add(new Video("V001", "thanhnam", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur maximus urna nec sem rhoncus, scelerisque sodales felis tincidunt."
                , "video3135074881", 312, 243, 99999, new Date()));
        videos.add(new Video("V002", "ngoctrung", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur maximus urna nec sem rhoncus, scelerisque sodales felis tincidunt."
                , "video3135074881", 43523, 2342, 99999, new Date()));
        videos.add(new Video("V003", "hoaitan", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur maximus urna nec sem rhoncus, scelerisque sodales felis tincidunt."
                , "video3135074881", 12323, 234, 99999, new Date()));

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        videos = new ArrayList<>();
        initData();
        VideoAdapter videoAdapter = new VideoAdapter(videos, view.getContext());
        recyclerView.setAdapter(videoAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Set status bar color
        requireActivity().getWindow().setStatusBarColor(Color.BLACK);
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(0);

        // Inflate the layout for this fragment
        return view;
    }


}