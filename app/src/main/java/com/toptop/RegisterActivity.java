package com.toptop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.toptop.fragment.InputAccountRegister;
import com.toptop.fragment.InputInfoRegister;
import com.toptop.models.User;
import com.toptop.utils.firebase.UserFirebase;

public class RegisterActivity extends AppCompatActivity {
	public static final String USER = "user";
	// tag
	private static final String TAG = "RegisterActivity";

	public User newUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		getWindow().setStatusBarColor(Color.WHITE);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.input_fragment, new InputInfoRegister(), InputInfoRegister.TAG)
				.addToBackStack(InputInfoRegister.TAG)
				.add(R.id.input_fragment, new InputAccountRegister(), InputAccountRegister.TAG)
				.addToBackStack(InputAccountRegister.TAG)
				.commit();

		//after transaction you must call the executePendingTransaction
		getSupportFragmentManager().executePendingTransactions();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.input_fragment, getSupportFragmentManager().findFragmentByTag(InputInfoRegister.TAG))
				.commit();

		TextView txt_login = findViewById(R.id.txt_login);
		txt_login.setOnClickListener(v -> {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(MainActivity.EXTRA_LOGIN, true);
			setResult(RESULT_CANCELED, intent);
			finish();
		});

		newUser = new User();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		super.onBackPressed();
	}

	// Finish activity
	public void finishRegister(boolean isSuccess) {
		User user = newUser;
		Intent intent = new Intent(this, MainActivity.class);
		if (isSuccess) {
			// Create new user
			UserFirebase.addUser(user);
			intent.putExtra(USER, user);
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED, intent);
		}
		finish();
	}
}