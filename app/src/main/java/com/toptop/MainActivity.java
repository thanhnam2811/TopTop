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

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void setTranslucentStatusBar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTranslucentStatusBar();

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