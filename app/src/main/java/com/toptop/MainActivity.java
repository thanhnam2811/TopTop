package com.toptop;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.toptop.adapters.VideoAdapter;
import com.toptop.models.Video;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment = null;
    private List<Video> videoList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        // Remove the action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // Bind the view using the id
        chipNavigationBar = findViewById(R.id.chipNavigationBar);

        // Set videos fragment as default
        chipNavigationBar.setItemSelected(R.id.video, true);
        fragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.recyclerView, fragment).commit();

        // Set listener
        chipNavigationBar.setOnItemSelectedListener(i -> {
            switch (i) {
                case R.id.video:
                    fragment = new VideoFragment();
                    break;
                case R.id.search:
                    fragment = new SearchFragment();
                    break;
                case R.id.notification:
                    fragment = new NotificationFragment();
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    break;
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.recyclerView, fragment).commit();
            }
        });

        // RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        videoList.add(new Video("", "", "", "", 5, 5, 5, new Date()));
        videoList.add(new Video("", "", "", "", 5, 5, 5, new Date()));
        videoList.add(new Video("", "", "", "", 5, 5, 5, new Date()));
        videoList.add(new Video("", "", "", "", 5, 5, 5, new Date()));
        videoList.add(new Video("", "", "", "", 5, 5, 5, new Date()));

        videoAdapter = new VideoAdapter(videoList, getApplicationContext());
        recyclerView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
    }
}