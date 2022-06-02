package com.toptop;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.toptop.fragment.DashboardFragment;
import com.toptop.fragment.UsersManagementFragment;
import com.toptop.fragment.VideosManagementFragment;
import com.toptop.utils.MyUtil;

public class AdminActivity extends FragmentActivity {
	NavigationView leftNav;
	ImageView icMenu;
	Fragment activeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		MyUtil.setLightStatusBar(this);

		// Set navigation bar color
		getWindow().setNavigationBarColor(Color.WHITE);


		leftNav = findViewById(R.id.nav_view);
		icMenu = findViewById(R.id.ic_menu);
		DrawerLayout drawer = findViewById(R.id.drawer_layout);

		icMenu.setOnClickListener(v -> drawer.open());

		leftNav.setNavigationItemSelectedListener(menuItem -> {
			switch (menuItem.getItemId()) {
				case R.id.dashboard:
					if (activeFragment != DashboardFragment.getInstance()) {
						getSupportFragmentManager().beginTransaction()
								.hide(activeFragment)
								.show(DashboardFragment.getInstance())
								.commit();
						activeFragment = DashboardFragment.getInstance();
					}
					break;
				case R.id.users:
					if (activeFragment != UsersManagementFragment.getInstance()) {
						getSupportFragmentManager().beginTransaction()
								.hide(activeFragment)
								.show(UsersManagementFragment.getInstance())
								.commit();
						activeFragment = UsersManagementFragment.getInstance();
					}
					break;
				case R.id.videos:
					if (activeFragment != VideosManagementFragment.getInstance()) {
						getSupportFragmentManager().beginTransaction()
								.hide(activeFragment)
								.show(VideosManagementFragment.getInstance())
								.commit();
						activeFragment = VideosManagementFragment.getInstance();
					}
					break;
			}
			drawer.close();
			return true;
		});

		// Init Fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, DashboardFragment.getInstance())
				.hide(DashboardFragment.getInstance())
				.add(R.id.fragment_container, UsersManagementFragment.getInstance())
				.hide(UsersManagementFragment.getInstance())
				.add(R.id.fragment_container, VideosManagementFragment.getInstance())
				.hide(VideosManagementFragment.getInstance())
				.commit();

		// Show Dashboard Fragment
		getSupportFragmentManager().beginTransaction()
				.show(DashboardFragment.getInstance())
				.commit();
		activeFragment = DashboardFragment.getInstance();
		leftNav.setCheckedItem(R.id.dashboard);
	}
}