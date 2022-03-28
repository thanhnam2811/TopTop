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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        // Remove the action bar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        // Bind the view using the id
        chipNavigationBar = findViewById(R.id.chipNavigationBar);

        // Set videos fragment as default
        chipNavigationBar.setItemSelected(R.id.video, true);
        fragment = new VideoFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        // Set listener
        chipNavigationBar.setOnItemSelectedListener(i -> {
            if (i == R.id.video)
                fragment = new VideoFragment();
            else if (i == R.id.search)
                fragment = new SearchFragment();
            else if (i == R.id.notification)
                fragment = new NotificationFragment();
            else if (i == R.id.profile)
                fragment = new ProfileFragment();

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });
    }
}