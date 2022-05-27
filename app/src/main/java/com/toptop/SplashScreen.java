package com.toptop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.toptop.utils.MyUtil;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		MyUtil.setDarkStatusBar(this);

		new Handler().postDelayed(() -> {
			// Start the next activity
			Intent intent = new Intent(SplashScreen.this, MainActivity.class);
			startActivity(intent);
			finish();
		}, 1000);

	}
}