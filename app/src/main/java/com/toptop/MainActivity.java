package com.toptop;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.WindowManager;

import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;

import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

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

        // Bind the view using the id
        CurvedBottomNavigationView nav = findViewById(R.id.nav);

        // Set list of items
        CbnMenuItem[] items = new CbnMenuItem[]{
                new CbnMenuItem(R.drawable.ic_video, R.drawable.ic_video_avd, 0),
                new CbnMenuItem(R.drawable.ic_search, R.drawable.ic_search_avd, 0),
                new CbnMenuItem(R.drawable.ic_notification, R.drawable.ic_notification_avd, 0),
                new CbnMenuItem(R.drawable.ic_profile, R.drawable.ic_profile_avd, 0)
        };

        // Set the items
        nav.setMenuItems(items, 0);

        // Set the listener
        nav.setOnMenuItemClickListener((cbnMenuItem, integer) -> {
            switch (cbnMenuItem.getIcon()) {
                case R.drawable.ic_video:
                    fragment = new VideoFragment();
                    break;
                case R.drawable.ic_search:
                    fragment = new SearchFragment();
                    break;
                case R.drawable.ic_notification:
                    fragment = new NotificationFragment();
                    break;
                case R.drawable.ic_profile:
                    fragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return null;
        });

        // Set the default selected item
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideoFragment()).commit();

        getPermission();
    }

    private void getPermission() {
        // Get INTERNET permission if needed
        if (PermissionChecker.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED)
            requestPermissions(new String[]{android.Manifest.permission.INTERNET}, 1);

    }
}