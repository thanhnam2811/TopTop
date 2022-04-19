package com.toptop;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;

public class MainActivity extends FragmentActivity {

    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Bind the view using the id
        ChipNavigationBar chipNavigationBar = findViewById(R.id.chipNavigationBar);

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

        getPermission();
    }

    private void getPermission() {
        // Get INTERNET permission if needed
        if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1);


    }
}