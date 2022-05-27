package com.toptop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.toptop.fragment.DashboardFragment;
import com.toptop.utils.MyUtil;

public class AdminActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		MyUtil.setLightStatusBar(this);

		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, DashboardFragment.getInstance()).commit();
	}
}