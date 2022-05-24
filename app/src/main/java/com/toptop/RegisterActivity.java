package com.toptop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.toptop.fragment.InputAccountRegister;
import com.toptop.fragment.InputInfoRegister;
import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
	public static final String USER = "user";
	// tag
	private static final String TAG = "RegisterActivity";

	public User newUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, this);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.input_fragment, InputInfoRegister.getInstance(), InputInfoRegister.TAG)
				.addToBackStack(InputInfoRegister.TAG)
				.add(R.id.input_fragment, InputAccountRegister.getInstance(), InputAccountRegister.TAG)
				.addToBackStack(InputAccountRegister.TAG)
				.commit();

		//after transaction you must call the executePendingTransaction
		getSupportFragmentManager().executePendingTransactions();

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.input_fragment, Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(InputInfoRegister.TAG)))
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
	public void finishRegister(boolean isSuccess, String email, String password) {
		FirebaseAuth auth = FirebaseAuth.getInstance();
		Intent intent = new Intent(this, MainActivity.class);
		Log.i(TAG, "finishRegister: email: " + email + " password: " + password);
		if (isSuccess) {
			auth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(this, task -> {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.i(TAG, "createUserWithEmail:success");
							FirebaseUser user = auth.getCurrentUser();
							if (user != null) {
								// Create new user
								newUser.setUid(user.getUid());
								newUser.setEmail(user.getEmail());
								if (user.getPhotoUrl() != null) {
									newUser.setAvatar(user.getPhotoUrl().toString());
								}
								UserFirebase.addUser(newUser);
								intent.putExtra(USER, newUser);
								setResult(RESULT_OK, intent);
								finish();
							}
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "createUserWithEmail:failure", task.getException());
							Toast.makeText(RegisterActivity.this, "Authentication failed.",
									Toast.LENGTH_SHORT).show();
						}
						// ...
					});
		} else {
			setResult(RESULT_CANCELED, intent);
			finish();
		}
	}
}