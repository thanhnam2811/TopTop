package com.toptop;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.toptop.fragment.NotificationFragment;
import com.toptop.fragment.ProfileFragment;
import com.toptop.fragment.SearchFragment;
import com.toptop.fragment.VideoFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

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
    }
}