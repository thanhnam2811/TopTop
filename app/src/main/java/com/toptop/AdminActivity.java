package com.toptop;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.navigation.NavigationView;
import com.toptop.fragment.DashboardFragment;
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
			}
			return true;
		});

		// Init Fragment
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, DashboardFragment.getInstance())
				.hide(DashboardFragment.getInstance())
				.commit();

		// Show Dashboard Fragment
		getSupportFragmentManager().beginTransaction()
				.show(DashboardFragment.getInstance())
				.commit();
		activeFragment = DashboardFragment.getInstance();
		leftNav.setCheckedItem(R.id.dashboard);
	}
}